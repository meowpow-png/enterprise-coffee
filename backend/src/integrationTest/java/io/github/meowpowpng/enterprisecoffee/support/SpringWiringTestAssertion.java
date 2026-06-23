package io.github.meowpowpng.enterprisecoffee.support;

import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public final class SpringWiringTestAssertion {

    private final AssertableApplicationContext context;

    private SpringWiringTestAssertion(AssertableApplicationContext context) {
        this.context = context;
    }

    public static SpringWiringTestAssertion assertThatContext(AssertableApplicationContext context) {
        return new SpringWiringTestAssertion(context);
    }

    public SpringWiringTestAssertion starts() {
        var message = "Expected application context to start successfully, but it failed";
        assertThat(context).as(message).hasNotFailed();

        return this;
    }

    public SpringWiringTestAssertion hasSingleBean(Class<?> beanType) {
        var message = "Expected exactly one bean of type %s, but found none or multiple";
        assertThat(context)
                .as(message.formatted(beanType.getSimpleName()))
                .hasSingleBean(beanType);

        return this;
    }

    public SpringWiringTestAssertion hasBean(String beanName) {
        var message = "Expected bean named '%s' to be present in context, but it was not found";
        assertThat(context)
                .as(message.formatted(beanName))
                .hasBean(beanName);

        return this;
    }

    public SpringWiringTestAssertion doesNotHaveBean(Class<?> beanType) {
        var message = "Expected no beans of type %s to be present in context, but at least one was found";
        assertThat(context)
                .as(message.formatted(beanType.getSimpleName()))
                .doesNotHaveBean(beanType);

        return this;
    }

    public SpringWiringTestAssertion hasScheduledBean(Class<?> beanType) {
        var message = "Expected scheduled bean of type %s to be present in context";
        assertThat(context)
                .as(message.formatted(beanType.getSimpleName()))
                .hasSingleBean(beanType);

        return this;
    }

    public SpringWiringTestAssertion usesCronFrom(String cronBeanName) {
        var cron = context.getBean(cronBeanName, String.class);

        var cronTasks = postProcessor().getScheduledTasks().stream()
                .map(ScheduledTask::getTask)
                .filter(CronTask.class::isInstance)
                .map(CronTask.class::cast)
                .toList();

        var m1 = "Expected at least one scheduled cron task to be registered";
        assertThat(cronTasks).as(m1).isNotEmpty();

        var m2 = "Expected at least one scheduled task to use cron expression from bean '%s'";
        assertThat(cronTasks)
                .as(m2.formatted(cronBeanName))
                .anyMatch(task -> task.getExpression().equals(cron));

        return this;
    }

    public SpringWiringTestAssertion hasScheduledMethod(Class<?> beanType, String methodName) {
        var scheduledMethods = postProcessor().getScheduledTasks().stream()
                .map(ScheduledTask::getTask)
                .map(Object::toString)
                .toList();

        var message = "Expected scheduled method %s.%s to be registered";
        assertThat(scheduledMethods)
                .as(message.formatted(beanType.getSimpleName(), methodName))
                .anyMatch(task -> isScheduledMethod(task, beanType, methodName));

        return this;
    }

    public SpringWiringTestAssertion isTransactionalProxy(Class<?> beanType) {
        var bean = context.getBean(beanType);
        var targetClass = AopUtils.getTargetClass(bean);

        List<String> nonTxMethods = findNonTransactionalMethods(targetClass);

        var className = targetClass.getSimpleName();
        var message = "Expected all public methods of %s to be transactional, but these were not: %s";

        assertThat(nonTxMethods)
                .as(message.formatted(className, nonTxMethods))
                .isEmpty();

        return this;
    }

    public SpringWiringTestAssertion hasTransactionalMethod(Class<?> beanType, String methodName) {
        var bean = context.getBean(beanType);
        var targetClass = AopUtils.getTargetClass(bean);

        var method = getMethodOrThrow(targetClass, methodName);

        var txAttrSource = context.getBeanFactory().getBean(TransactionAttributeSource.class);
        var attr = txAttrSource.getTransactionAttribute(method, targetClass);

        var clazzName = targetClass.getSimpleName();
        var message = "Expected method %s.%s to be transactional";
        assertThat(attr).as(message.formatted(clazzName, methodName)).isNotNull();

        return this;
    }

    AssertableApplicationContext getContext() {
        return context;
    }

    private ScheduledAnnotationBeanPostProcessor postProcessor() {
        return context.getBean(ScheduledAnnotationBeanPostProcessor.class);
    }

    private static boolean isScheduledMethod(
            String taskDescription,
            Class<?> beanType,
            String methodName
    ) {
        return taskDescription.contains(beanType.getSimpleName())
                && taskDescription.contains(methodName);
    }

    private List<String> findNonTransactionalMethods(Class<?> targetClass) {
        var txAttrSource = context.getBeanFactory().getBean(TransactionAttributeSource.class);

        var methods = Arrays.stream(targetClass.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> !m.isSynthetic())
                .toList();

        return methods.stream()
                .filter(method -> txAttrSource.getTransactionAttribute(method, targetClass) == null)
                .map(Method::getName)
                .toList();
    }

    private Method getMethodOrThrow(Class<?> type, String methodName) {
        // prefer methods declared on the class itself
        var declared = Arrays.stream(type.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst();

        // fallback to inherited/public methods
        return declared.orElseGet(() -> Arrays.stream(type.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> m.getDeclaringClass() != Object.class)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Method not found: " + type.getSimpleName() + "." + methodName
                )));
    }
}
