package com.placementportal.mapper;

import com.placementportal.dto.ExperienceResponse;
import com.placementportal.model.Experience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperienceMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    ExperienceResponse toResponse(Experience experience);
}
