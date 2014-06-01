package org.activityinfo.datamodel.rebind;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.activityinfo.datamodel.client.impl.DataRecordBeanFactory;
import org.activityinfo.datamodel.shared.DataRecordBean;

import java.io.PrintWriter;
import java.util.List;

public class FactoryClass {

    public static final String PACKAGE = "org.activityinfo.datamodel.client";
    public static final String SIMPLE_CLASS_NAME = "DataRecordBeanFactoryImpl";
    public static final String CLASS_NAME = PACKAGE + "." + SIMPLE_CLASS_NAME;


    private List<BeanClass> beanClasses;

    public FactoryClass(List<BeanClass> recordClasses) {
        this.beanClasses = recordClasses;
    }

    public void implement(TreeLogger logger, GeneratorContext generatorContext) throws UnableToCompleteException {

        PrintWriter out = generatorContext.tryCreate(logger, PACKAGE, SIMPLE_CLASS_NAME);

        // If an implementation already exists, we don't need to do any work
        if (out != null) {
            write(logger, generatorContext, out);
        }
    }

    public void write(TreeLogger logger, GeneratorContext generatorContext, PrintWriter out) throws UnableToCompleteException {

        ClassSourceFileComposerFactory f = new ClassSourceFileComposerFactory(
                PACKAGE, SIMPLE_CLASS_NAME);

        f.addImplementedInterface(DataRecordBeanFactory.class.getName());
        f.addImport(JavaScriptObject.class.getName());
        f.addImport(DataRecordBean.class.getName());

        SourceWriter sw = f.createSourceWriter(generatorContext, out);
        writeParseFromJson(sw);

        sw.commit(logger);
    }

    private void writeParseFromJson(SourceWriter sw) {

        sw.println("@Override");
        sw.println("public <T extends DataRecordBean> T create(Class<T> beanClass, JavaScriptObject jso) {");
        sw.indent();

        for(BeanClass beanClass : beanClasses) {
            sw.println(String.format("if(beanClass == %s) return (T)(%s)jso;",
                    beanClass.getQualifiedInterfaceSourceName() + ".class",
                    beanClass.getQualifiedImplementationSourceName()));
        }

        // it should not be possible to reach this point because we have provided
        // JSO impls for all DataRecordBean subtypes
        sw.println("return (T)jso;");
        sw.outdent();
        sw.println("}");
    }
}
