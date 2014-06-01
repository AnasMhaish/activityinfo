package org.activityinfo.datamodel.rebind;

import com.google.gwt.core.ext.*;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import org.activityinfo.datamodel.shared.DataRecordBean;

import java.util.List;

/**
 * Deferred Binding generator which creates JavaScript overlay objects
 * for interfaces which extend DataRecordBeans.
 */
public class DataRecordBeanGenerator extends IncrementalGenerator {

    public static final long VERSION = 2;

    @Override
    public long getVersionId() {
        return VERSION;
    }

    @Override
    public RebindResult generateIncrementally(TreeLogger logger,
                                              GeneratorContext ctx,
                                              String typeName) throws UnableToCompleteException {

        logger.log(TreeLogger.DEBUG, "Generating " + FactoryClass.CLASS_NAME);

        // Retrieve the TypeDataRecord interface, which is the marker
        // interface for all of the TypedDataRecord interfaces we will generate
        JClassType typedDataRecord = findRootInterface(logger, ctx);

        // Enumerate through the list of DataRecordBeans and see which implementations are
        // available in the cache.
        List<BeanClass> beanClasses = Lists.newArrayList();
        boolean completelyCached = true;
        for(JClassType subType : typedDataRecord.getSubtypes()) {
            TreeLogger subTypeLogger = logger.branch(TreeLogger.Type.DEBUG,
                    "DataRecordBean: " + subType.getName());

            BeanClass beanClass = new BeanClass(subType);

            if(ctx.tryReuseTypeFromCache(beanClass.getQualifiedImplementationSourceName())) {
                subTypeLogger.log(TreeLogger.Type.DEBUG, "Reused from cache");

            } else {
                completelyCached = false;
                beanClass.createImplementation(subTypeLogger, ctx);
            }

            beanClasses.add(beanClass);
        }

        // If we have all of the subtypes cached, and factory implementation is cached,
        // then in principle we should be able to reuse the last generated version.
        if(completelyCached && ctx.tryReuseTypeFromCache(FactoryClass.CLASS_NAME)) {
            return new RebindResult(RebindMode.USE_ALL_CACHED, FactoryClass.CLASS_NAME);

        } else {
            FactoryClass impl = new FactoryClass(beanClasses);
            impl.implement(logger, ctx);

            return new RebindResult(RebindMode.USE_PARTIAL_CACHED, FactoryClass.CLASS_NAME);
        }
    }

    private JClassType findRootInterface(TreeLogger logger,
                                         GeneratorContext generatorContext) throws UnableToCompleteException {
        try {
            return generatorContext.getTypeOracle().getType(DataRecordBean.class.getName());

        } catch (NotFoundException e) {
            logger.log(TreeLogger.Type.ERROR, "Can't find " + DataRecordBean.class.getName() +
                ": Something must be broken in org.activityinfo.datamodel.");
            throw new UnableToCompleteException();
        }
    }
}
