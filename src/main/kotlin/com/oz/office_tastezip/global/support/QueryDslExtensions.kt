package com.oz.office_tastezip.global.support

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.ComparableExpressionBase
import org.springframework.data.domain.Pageable

/**
 * Pageable의 Sort 정보를 QueryDSL의 OrderSpecifier 리스트로 변환한다.
 *
 * @param columnResolver 정렬 필드명(String)을 QueryDSL 표현식으로 매핑하는 함수
 * @return OrderSpecifier 리스트
 */
fun Pageable.toOrderSpecifiers(
    columnResolver: (String) -> ComparableExpressionBase<*>?
): List<OrderSpecifier<*>> {
    return sort.mapNotNull { sortOrder ->
        val direction = if (sortOrder.isAscending) Order.ASC else Order.DESC
        val expression = columnResolver(sortOrder.property)

        expression?.let { OrderSpecifier(direction, it) }
    }.toList()
}
