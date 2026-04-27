package com.vrp.infrastructure.cache.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.mockito.Mockito.mock

class RedisConfigTest {

    private val redisConfig = RedisConfig()

    @Test
    fun `should create redis template with json serializers`() {
        val connectionFactory = mock(RedisConnectionFactory::class.java)

        val template = redisConfig.redisTemplate(connectionFactory)

        assertThat(template).isNotNull()
        assertThat(template.connectionFactory).isEqualTo(connectionFactory)
        assertThat(template.keySerializer).isInstanceOf(StringRedisSerializer::class.java)
        assertThat(template.valueSerializer).isInstanceOf(RedisSerializer::class.java)
        assertThat(template.hashKeySerializer).isInstanceOf(StringRedisSerializer::class.java)
        assertThat(template.hashValueSerializer).isInstanceOf(RedisSerializer::class.java)
    }

    @Test
    fun `should create cache manager with default configuration`() {
        val connectionFactory = mock(RedisConnectionFactory::class.java)

        val cacheManager = redisConfig.cacheManager(connectionFactory)

        assertThat(cacheManager).isNotNull()
        assertThat(cacheManager.cacheNames).isEmpty()
    }

    @Test
    fun `redis template should use same serializer for value and hash value`() {
        val connectionFactory = mock(RedisConnectionFactory::class.java)

        val template = redisConfig.redisTemplate(connectionFactory)

        // Both should use RedisSerializer.json() which returns the same type
        assertThat(template.valueSerializer).isNotNull()
        assertThat(template.hashValueSerializer).isNotNull()
        assertThat(template.valueSerializer.javaClass).isEqualTo(template.hashValueSerializer.javaClass)
    }
}
