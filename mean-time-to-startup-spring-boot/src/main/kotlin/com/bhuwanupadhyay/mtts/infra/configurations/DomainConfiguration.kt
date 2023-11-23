package com.bhuwanupadhyay.mtts.infra.configurations

import com.bhuwanupadhyay.mtts.domain.BoxOffice
import com.bhuwanupadhyay.mtts.domain.api.DomainService
import com.bhuwanupadhyay.mtts.domain.stubs.Stub
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ANNOTATION

@Configuration
@ComponentScan(
        basePackageClasses = [BoxOffice::class],
        includeFilters = [Filter(type = ANNOTATION, value = [DomainService::class, Stub::class])])
class DomainConfiguration