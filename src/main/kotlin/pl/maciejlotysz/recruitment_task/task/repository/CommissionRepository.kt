package pl.maciejlotysz.recruitment_task.task.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pl.maciejlotysz.recruitment_task.task.entity.Commission

@Repository
interface CommissionRepository : MongoRepository<Commission, String>
