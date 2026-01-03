package com.amalixshop.services;

import com.amalixshop.models.Product;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ProductSearchService {
    private List<Product> productCache;
    private final Map<String, List<Product>> searchCache = new ConcurrentHashMap<>();
    private final Map<String, List<Product>> categoryCache = new ConcurrentHashMap<>();
    private final Object cacheLock = new Object();

    // For autocomplete suggestions
    private final Map<String, List<String>> suggestionCache = new ConcurrentHashMap<>();

    public void initializeCache(List<Product> products) {
        synchronized (cacheLock) {
            this.productCache = new ArrayList<>(products);
            buildCaches();
        }
    }

    private void buildCaches() {
        // Build category cache
        for (Product product : productCache) {
            String category = product.getCategoryName();
            categoryCache.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
        }

        // Build suggestion cache (first 3 letters for autocomplete)
        for (Product product : productCache) {
            String name = product.getProductName().toLowerCase();
            for (int i = 1; i <= Math.min(3, name.length()); i++) {
                String prefix = name.substring(0, i);
                suggestionCache.computeIfAbsent(prefix, k -> new ArrayList<>())
                        .add(product.getProductName());
            }
        }
    }

    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(productCache);
        }

        String normalizedQuery = query.toLowerCase().trim();

        // Check cache first
        if (searchCache.containsKey(normalizedQuery)) {
            return new ArrayList<>(searchCache.get(normalizedQuery));
        }

        // Perform parallel search
        List<Product> results = productCache.parallelStream()
                .filter(product -> matchesQuery(product, normalizedQuery))
                .collect(Collectors.toList());

        // Cache the results
        searchCache.put(normalizedQuery, new ArrayList<>(results));

        return results;
    }

    private boolean matchesQuery(Product product, String query) {
        return product.getProductName().toLowerCase().contains(query) ||
                product.getDescription().toLowerCase().contains(query) ||
                product.getCategoryName().toLowerCase().contains(query);
    }

    public List<Product> filterByCategory(String category) {
        if ("All Categories".equals(category) || category == null) {
            return new ArrayList<>(productCache);
        }

        return new ArrayList<>(categoryCache.getOrDefault(category, new ArrayList<>()));
    }

    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        return productCache.parallelStream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<Product> filterByCategoryAndPrice(String category, double minPrice, double maxPrice) {
        List<Product> categoryProducts = filterByCategory(category);

        return categoryProducts.parallelStream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<String> getSuggestions(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>();
        }

        String normalizedPrefix = prefix.toLowerCase();
        Set<String> suggestions = new LinkedHashSet<>();

        // Get exact prefix matches
        suggestions.addAll(suggestionCache.getOrDefault(normalizedPrefix, new ArrayList<>()));

        // Also get suggestions for similar prefixes
        suggestionCache.keySet().stream()
                .filter(key -> key.startsWith(normalizedPrefix))
                .map(suggestionCache::get)
                .flatMap(List::stream)
                .forEach(suggestions::add);

        return new ArrayList<>(suggestions).stream()
                .limit(10) // Limit to 10 suggestions
                .collect(Collectors.toList());
    }

    public void refreshCache(List<Product> newProducts) {
        synchronized (cacheLock) {
            this.productCache = new ArrayList<>(newProducts);
            searchCache.clear();
            categoryCache.clear();
            suggestionCache.clear();
            buildCaches();
        }
    }

    public int getCacheSize() {
        return productCache != null ? productCache.size() : 0;
    }
}