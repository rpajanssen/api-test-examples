package com.example.examples.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.TableGenerator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Custom ID generator that allows us to add new entities with a given ID where hibernate won't overwrite it!
 *
 * It assumes there is a property id with a getter getId. If it fails... it will delegate to hibernate.
 *
 * We need this custom generator for testing purposes. Using Karate we setup/cleanup the test state from Karate using
 * the real API's. So we fill and empty the database using the API's actually under test. Therefore need to control the
 * ID generation otherwise it will be difficult to write tests.
 *
 * Another option would be to write a utility class that manipulates the DB state and to call that from Karate.
 */
public class IdGenerator extends TableGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor implementor, Object object) throws HibernateException {
        try {
            Method getIdMethod = object.getClass().getMethod("getId");
            Object idValue = getIdMethod.invoke(object);
            if (idValue != null) {
                return (Serializable) idValue;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // swallow... let hibernate have its way
        }

        return super.generate(implementor, object);
    }
}
