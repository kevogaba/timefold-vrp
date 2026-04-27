package com.example.vrp.job.adapter.out.cache

import com.example.vrp.job.domain.Job
import com.example.vrp.job.port.out.JobCachePort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisJobCacheAdapter(
    private val redisTemplate: RedisTemplate<String, Job>,
) : JobCachePort {

    private fun key(organizationId: String, jobId: String) = "$organizationId:job:$jobId"

    override fun get(organizationId: String, jobId: String): Job? =
        redisTemplate.opsForValue().get(key(organizationId, jobId))

    override fun put(organizationId: String, job: Job) {
        redisTemplate.opsForValue().set(key(organizationId, job.id), job, TTL)
    }

    override fun evict(organizationId: String, jobId: String) {
        redisTemplate.delete(key(organizationId, jobId))
    }

    companion object {
        private val TTL = Duration.ofHours(24)
    }
}
