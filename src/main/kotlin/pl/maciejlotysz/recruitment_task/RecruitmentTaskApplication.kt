package pl.maciejlotysz.recruitment_task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class RecruitmentTaskApplication

fun main(args: Array<String>) {
	runApplication<RecruitmentTaskApplication>(*args)
}
