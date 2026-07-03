package com.ecommerce.common;

import java.util.List;

public record PagedResponse<T>(
    List<T> content
) {
}
