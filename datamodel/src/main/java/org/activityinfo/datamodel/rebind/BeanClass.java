package org.activityinfo.datamodel.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.thirdparty.guava.common.base.Optional;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.activityinfo.datamodel.client.impl.DataRecordJsoImpl;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.datamodel.shared.DataRecordBean;

import java.io.PrintWriter;

/**
 * Creates implementations of subtypes of {@code DataRecordBean}
 *
 */
public class BeanClass {

    public static final String IMPL_SUFFIX = "_JsoImpl";
    private JClassType beanInterface;
    private final String packageName;
    private final String generatedSimpleSourceName;

    public BeanClass(JClassType sourceType) {
        this.beanInterface = sourceType;
        packageName = sourceType.getPackage().getName();
        generatedSimpleSourceName = sourceType.getSimpleSourceName() + IMPL_SUFFIX;
    }

    public JClassType getInterface() {
        return beanInterface;
    }


    /**
     * @return the full name of the interface for which we will generate
     * an implementation
     */
    public String getQualifiedInterfaceSourceName() {
        return beanInterface.getQualifiedSourceName();
    }

    public String getQualifiedImplementationSourceName() {
        return packageName + "." + generatedSimpleSourceName;
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the name of the implementation class, without the package or the
     * enclosing class
     */
    public String getGeneratedSimpleSourceName() {
        return generatedSimpleSourceName;
    }

    public void createImplementation(TreeLogger logger, GeneratorContext generatorContext) throws
            UnableToCompleteException {

        logger.log(TreeLogger.Type.DEBUG, "Creating implementation class " + getQualifiedImplementationSourceName());

        PrintWriter out = generatorContext.tryCreate(logger, packageName, generatedSimpleSourceName);

        // If an implementation already exists, we don't need to do any work
        if (out == null) {
            logger.log(TreeLogger.Type.DEBUG, "Class already exists");

        } else {
            write(logger, generatorContext, out);
        }
    }

    public void write(TreeLogger logger, GeneratorContext generatorContext, PrintWriter out) throws UnableToCompleteException {

        ClassSourceFileComposerFactory f = new ClassSourceFileComposerFactory(
                packageName, generatedSimpleSourceName);

        // Extend DataRecordJsoImpl to implement the getter/setter methods
        f.setSuperclass(DataRecordJsoImpl.class.getName());
        f.addImplementedInterface(beanInterface.getQualifiedSourceName());

        // All source gets written through this Writer
        SourceWriter sw = f.createSourceWriter(generatorContext, out);
        write(logger, sw);

        sw.commit(logger);
    }


    public void write(TreeLogger parentLogger, SourceWriter sw) throws UnableToCompleteException {

        // constructor
        writeRequiredJsoProtectedConstructor(sw, generatedSimpleSourceName);

        // now implement getters/setters
        for(JMethod method : beanInterface.getMethods()) {
            TreeLogger logger = parentLogger.branch(TreeLogger.Type.DEBUG,
                    "Writing implementation for " + method.getName());

            Optional<String> fieldId = getGetterName(method);
            if(fieldId.isPresent()) {
                sw.println("public final " + method.getReadableDeclaration(true, true, true, true, true) + " {");
                sw.indent();
                writeGetterBody(logger, sw, fieldId.get(), method.getReturnType());
                sw.outdent();
                sw.println("}");
            }
        }
    }

    private void writeGetterBody(TreeLogger logger, SourceWriter sw, String fieldId, JType returnType) throws UnableToCompleteException {
        if(returnType.getQualifiedSourceName().equals(Cuid.class.getName()) ||
           returnType.getQualifiedSourceName().equals(String.class.getName())) {

            sw.println("return getString(\"%s\");", fieldId);

        } else if(isList(returnType)) {
            sw.println("return (java.util.List)super.<%s>getList(\"%s\");",
                    getListElementType(logger, returnType), fieldId);

        } else {
            throw new UnableToCompleteException();
        }
    }

    private boolean isList(JType returnType) {
        return returnType.getQualifiedSourceName().equals("java.util.List");
    }

    private String getListElementType(TreeLogger logger, JType listType) throws UnableToCompleteException {
        JParameterizedType genericType = listType.isParameterized();
        if(genericType == null) {
            logger.log(TreeLogger.Type.ERROR, "List fields must be parametrized: found " +
                    listType.getQualifiedSourceName());
            throw new UnableToCompleteException();
        }

        JType elementType = genericType.getTypeArgs()[0];
        JClassType beanInterface = elementType.isInterface();
        if(beanInterface == null || !isDataRecordBean(beanInterface)) {
            logger.log(TreeLogger.Type.ERROR, "List fields must be parametrized with an interface that " +
                                              "is a subtype of DataRecordBean. Found: " +
                                              elementType.getQualifiedSourceName());
            throw new UnableToCompleteException();
        }

        return beanInterface.getQualifiedSourceName() + IMPL_SUFFIX;
    }

    private boolean isDataRecordBean(JClassType interfaceClass) {
        for(JClassType superType : interfaceClass.getImplementedInterfaces()) {
            if(superType.getQualifiedSourceName().equals(DataRecordBean.class.getName())) {
                return true;
            }
        }
        return false;
    }

    private Optional<String> getGetterName(JMethod method) {
        if( method.getParameters().length == 0 &&
            !method.isStatic() &&
            method.getName().startsWith("get")) {

            String name = method.getName().substring("get".length());
            if(name.length() > 0) {
                String fieldName = name.substring(0, 1).toLowerCase() + name.substring(1);
                return Optional.of(fieldName);
            }
        }
        return Optional.absent();
    }

    private void writeRequiredJsoProtectedConstructor(SourceWriter sw, String generatedSimpleSourceName) {
        sw.println("protected " + generatedSimpleSourceName + "() {}");
    }
}
