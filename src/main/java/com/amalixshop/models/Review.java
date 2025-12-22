package com.amalixshop.models;

import java.sql.Timestamp;

public class Review {
    private final int reviewId;
    private final int productId;
    private final int customerId;
    private final int rating;
    private final String comment;
    private final Timestamp reviewDate;
    private final boolean isApproved;

    public Review(int reviewId, int productId, int customerId, int rating,
                  String comment, Timestamp reviewDate, boolean isApproved) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.isApproved = isApproved;
    }

    // Getters only
    public int getReviewId() { return reviewId; }
    public int getProductId() { return productId; }
    public int getCustomerId() { return customerId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Timestamp getReviewDate() { return reviewDate; }
    public boolean isApproved() { return isApproved; }
}