package com.android.vndk;

import soot.Body;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.tagkit.SourceFileTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;

public class ApkAnalyzer {
    public static void main(String[] args) throws IOException {
        String androidJar = args[0];
        String appPath = args[1];

        G.v().out = new PrintStream(new NullOutputStream());

        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_force_android_jar(androidJar);
        Options.v().set_process_dir(Collections.singletonList(appPath));
        Options.v().set_process_multiple_dex(true);
        Options.v().set_keep_line_number(true);

        Scene.v().loadNecessaryClasses();

        Chain<SootClass> chain = Scene.v().getApplicationClasses();
        Formatter formatter = new Formatter();
        for (SootClass sootClass: chain) {
            SourceFileTag tag =
                    (SourceFileTag)sootClass.getTag("SourceFileTag");
            for (SootMethod sootMethod : sootClass.getMethods()) {
                if (sootMethod.isAbstract() || sootMethod.isNative()) {
                    continue;
                }
                Body b = sootMethod.retrieveActiveBody();
                UnitGraph g = new ExceptionalUnitGraph(b);
                Flow myFlow = new Flow(g, formatter, sootClass.getName(),
                                       sootMethod.getName(), tag.toString());
            }
        }
        formatter.print();
   }
}

