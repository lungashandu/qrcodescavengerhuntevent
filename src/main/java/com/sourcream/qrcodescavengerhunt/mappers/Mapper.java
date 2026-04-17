package com.sourcream.qrcodescavengerhunt.mappers;

public interface Mapper<A, B> {

    B mapTo(A a);

    A mapFrom(B b);
}
