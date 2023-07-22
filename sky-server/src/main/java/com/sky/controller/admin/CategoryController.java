package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/category")
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "添加分类")
    public Result<String> addCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询分类")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用分类
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用禁用分类")
    public Result<String> updateStatus(@PathVariable Integer status, Long id) {
        categoryService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    @ApiOperation(value = "删除分类")
    public Result<String> delete(Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping
    @ApiOperation(value = "修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }
}
