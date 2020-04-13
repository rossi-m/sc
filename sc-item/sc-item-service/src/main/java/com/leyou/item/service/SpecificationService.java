package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper mapper;

    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = mapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)){         //如果提供的集合为空返回false
            //没查到
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParamList(Long gid,Long cid,Boolean searching) {
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = paramMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;

    }

    public List<SpecGroup> queryListByCid(Long cid) {
        //查询组
        List<SpecGroup> specGroups = queryGroupByCid(cid);

        //查询当前分类下的参数
        List<SpecParam> specParams = queryParamList(null, cid, null);

        //先把规格参数变成map，map的key是规格组的id，map的值是组下的所有参数
        Map<Long,List<SpecParam>> map=new HashMap<>();
        for (SpecParam param: specParams){
            if (!map.containsKey(param.getGroupId())){  //如果此映射包含指定键的映射，则返回 true 。map中的key不包含组id
                //这个组id在map中不存在，新增一个list
                map.put(param.getGroupId(),new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param); //根据map集合的key，去添加list集合数据
        }
        //填充param到group
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }


        return specGroups;
    }

}
