package core.framework.impl.validate.type;

import core.framework.api.json.Property;
import core.framework.impl.reflect.Classes;
import core.framework.impl.reflect.Fields;
import core.framework.util.Maps;
import core.framework.util.Sets;
import core.framework.util.Strings;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static core.framework.util.Strings.format;

/**
 * @author neo
 */
public class JSONClassValidator implements TypeVisitor {
    public static void validateEnum(Class<?> enumClass) {
        Set<String> enumValues = Sets.newHashSet();
        List<Field> fields = Classes.enumConstantFields(enumClass);
        for (Field field : fields) {
            Property property = field.getDeclaredAnnotation(Property.class);
            if (property == null)
                throw new Error(format("enum must have @Property, field={}", Fields.path(field)));
            boolean added = enumValues.add(property.name());
            if (!added)
                throw new Error(format("found duplicate property, field={}, name={}", Fields.path(field), property.name()));
        }
    }

    private final DataTypeValidator validator;
    private final Map<String, Set<String>> properties = Maps.newHashMap();

    public JSONClassValidator(Class<?> instanceClass) {
        validator = new DataTypeValidator(instanceClass);
        validator.allowedValueClasses = Set.of(String.class, Boolean.class,
                Integer.class, Long.class, Double.class, BigDecimal.class,
                LocalDate.class, LocalDateTime.class, ZonedDateTime.class, Instant.class);
        validator.allowChild = true;
        validator.visitor = this;
    }

    public void validate() {
        validator.validate();
    }

    @Override
    public void visitField(Field field, String parentPath) {
        Property property = field.getDeclaredAnnotation(Property.class);
        if (property == null)
            throw new Error(format("field must have @Property, field={}", Fields.path(field)));

        String name = property.name();

        if (Strings.isEmpty(name)) {
            throw new Error(format("@Property name attribute must not be empty, field={}", Fields.path(field)));
        }

        boolean added = this.properties.computeIfAbsent(parentPath, key -> Sets.newHashSet()).add(name);
        if (!added) {
            throw new Error(format("found duplicate property, field={}, name={}", Fields.path(field), name));
        }
    }

    @Override
    public void visitEnum(Class<?> enumClass, String parentPath) {
        validateEnum(enumClass);
    }
}