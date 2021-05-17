/*
 * MATLAB Compiler: 7.0 (R2018b)
 * Date: Tue May  5 20:54:56 2020
 * Arguments: 
 * "-B""macro_default""-W""java:SumFunction,Class1""-T""link:lib""-d""D:\\WorkFiles\\Progress\\Matlab\\SumFunction\\for_testing""class{Class1:D:\\WorkFiles\\Progress\\Matlab\\SumFunction.m}"
 */

package SumFunction;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class SumFunctionMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "SumFunction_082552810557C5784882FA86554BA468";
    
    /** Component name */
    private static final String sComponentName = "SumFunction";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(SumFunctionMCRFactory.class)
        );
    
    
    private SumFunctionMCRFactory()
    {
        // Never called.
    }
    
    public static MWMCR newInstance(MWComponentOptions componentOptions) throws MWException
    {
        if (null == componentOptions.getCtfSource()) {
            componentOptions = new MWComponentOptions(componentOptions);
            componentOptions.setCtfSource(sDefaultComponentOptions.getCtfSource());
        }
        return MWMCR.newInstance(
            componentOptions, 
            SumFunctionMCRFactory.class, 
            sComponentName, 
            sComponentId,
            new int[]{9,5,0}
        );
    }
    
    public static MWMCR newInstance() throws MWException
    {
        return newInstance(sDefaultComponentOptions);
    }
}
