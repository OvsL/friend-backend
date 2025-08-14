package org.example.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.friend.model.domain.Tag;
import org.example.friend.service.TagService;
import org.example.friend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author Qi
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2025-07-22 14:32:01
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




