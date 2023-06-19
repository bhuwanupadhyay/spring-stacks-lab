package com.bhuwanupadhyay.mtts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MeanTimeToStartupSpringBootApplication

fun main(args: Array<String>) {
	runApplication<MeanTimeToStartupSpringBootApplication>(*args)
}
