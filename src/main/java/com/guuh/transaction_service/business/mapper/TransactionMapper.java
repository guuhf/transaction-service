package com.guuh.transaction_service.business.mapper;

import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.business.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    TransactionResponseDto toTransactionDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "categoryId", target = "category")
    Transaction toTransaction(TransactionRequestDto dto);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    List<TransactionResponseDto> toTransactionDtoList(List<Transaction> transactionList);

    default Category categoryMapper(Long id){
        Category category = new Category();
        category.setId(id);
        return category;
    }


}
