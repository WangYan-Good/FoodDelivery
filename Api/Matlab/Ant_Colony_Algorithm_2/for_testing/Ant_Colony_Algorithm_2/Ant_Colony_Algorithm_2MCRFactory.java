/*
 * MATLAB Compiler: 7.0 (R2018b)
 * Date: Fri May  8 16:42:35 2020
 * Arguments: 
 * "-B""macro_default""-W""java:Ant_Colony_Algorithm_2,Ant""-T""link:lib""-d""D:\\WorkFiles\\Progress\\Matlab\\Ant_Colony_Algorithm_2\\for_testing""class{Ant:D:\\WorkFiles\\Progress\\Matlab\\Ant_Colony_Algorithm_2.m}"
 */

package Ant_Colony_Algorithm_2;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class Ant_Colony_Algorithm_2MCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "Ant_Colony_A_87602D63BA9E9AB2677C0B7AF453D6FB";
    
    /** Component name */
    private static final String sComponentName = "Ant_Colony_Algorithm_2";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(Ant_Colony_Algorithm_2MCRFactory.class)
        );
    
    
    private Ant_Colony_Algorithm_2MCRFactory()
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
            Ant_Colony_Algorithm_2MCRFactory.class, 
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
