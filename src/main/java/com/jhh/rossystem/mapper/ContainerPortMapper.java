package com.jhh.rossystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jhh.rossystem.entity.ContainerPort;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ContainerPortMapper extends BaseMapper<ContainerPort> {
}
