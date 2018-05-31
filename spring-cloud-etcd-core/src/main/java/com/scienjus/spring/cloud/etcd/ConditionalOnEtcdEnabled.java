package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Conditional} for jetcd
 *
 * @author ScienJus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(ConditionalOnEtcdEnabled.OnEtcdEnabledCondition.class)
public @interface ConditionalOnEtcdEnabled {

    class OnEtcdEnabledCondition extends AllNestedConditions {

        public OnEtcdEnabledCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(value = "spring.cloud.etcd.enabled", matchIfMissing = true)
        static class FoundProperty {
        }

        @ConditionalOnClass(Client.class)
        static class FoundClass {
        }
    }
}
