package core.framework.impl.search;

import core.framework.api.util.Exceptions;
import core.framework.api.util.Maps;
import core.framework.api.util.Sets;
import core.framework.impl.validate.type.DataTypeValidator;
import core.framework.impl.validate.type.TypeVisitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author neo
 */
public final class ElasticSearchClassValidator implements TypeVisitor {
    private final DataTypeValidator validator;
    private final Map<Class, Set<String>> elements = Maps.newHashMap();

    public ElasticSearchClassValidator(Class<?> documentClass) {
        validator = new DataTypeValidator(documentClass);
        validator.allowedValueClass = this::allowedValueClass;
        validator.allowChildListAndMap = true;
        validator.allowChildObject = true;
        validator.visitor = this;
    }

    public void validate() {
        validator.validate();
    }

    private boolean allowedValueClass(Class<?> valueClass) {
        return String.class.equals(valueClass)
            || Integer.class.equals(valueClass)
            || Boolean.class.equals(valueClass)
            || Long.class.equals(valueClass)
            || Double.class.equals(valueClass)
            || BigDecimal.class.equals(valueClass)
            || LocalDate.class.equals(valueClass)
            || LocalDateTime.class.equals(valueClass)
            || Instant.class.equals(valueClass)
            || Enum.class.isAssignableFrom(valueClass);
    }

    @Override
    public void visitClass(Class<?> instanceClass, boolean topLevel) {
        XmlAccessorType accessorType = instanceClass.getDeclaredAnnotation(XmlAccessorType.class);
        if (accessorType == null || accessorType.value() != XmlAccessType.FIELD)
            throw Exceptions.error("document class must have @XmlAccessorType(XmlAccessType.FIELD), class={}", instanceClass);
    }

    @Override
    public void visitField(Field field, boolean topLevel) {
        XmlElement element = field.getDeclaredAnnotation(XmlElement.class);

        if (!Modifier.isPublic(field.getModifiers()) || element == null)
            throw Exceptions.error("all fields of document class must be public and with @XmlElement(name=), field={}", field);

        if (field.isAnnotationPresent(XmlTransient.class))
            throw Exceptions.error("document class field must not be transient, field={}", field);

        String name = element.name();

        if ("##default".equals(name)) {
            throw Exceptions.error("@XmlElement must have name attribute, field={}", field);
        }

        Set<String> elements = this.elements.computeIfAbsent(field.getDeclaringClass(), key -> Sets.newHashSet());
        if (elements.contains(name)) {
            throw Exceptions.error("element is duplicated, field={}, name={}", field, name);
        }
        elements.add(name);
    }
}
