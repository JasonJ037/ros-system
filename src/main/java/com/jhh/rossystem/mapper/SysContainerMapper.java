package com.jhh.rossystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jhh.rossystem.entity.SysContainer;
import com.jhh.rossystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SysContainerMapper extends BaseMapper<SysContainer> {

    List<SysUser> selectCountByUserIds(@Param(value = "userIds") List<Integer> userIds);

    Integer updateStatus(@Param(value = "status") Integer status,
                         @Param(value = "id") Integer id);

    Integer updateContainerId(@Param(value = "containerId") String containerId,
                              @Param(value = "id") Integer id);
}
