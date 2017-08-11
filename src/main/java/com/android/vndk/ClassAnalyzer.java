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

import org.jf.dexlib2.iface.ClassDef;

import java.io.PrintStream;

public class ClassAnalyzer {
    public static void main(String[] args) {
        G.v().out = new PrintStream(new NullOutputStream());

        Options.v().set_keep_line_number(true);

        Scene.v().setSootClassPath(Scene.v().defaultClassPath());
        Scene.v().extendSootClassPath(System.getProperty("java.class.path"));

        SootClass sootClass = Scene.v().loadClassAndSupport(args[0]);
        Scene.v().loadNecessaryClasses();

        SourceFileTag tag =
                (SourceFileTag)sootClass.getTag("SourceFileTag");

        sootClass.setApplicationClass();
        SootMethod sootMethod = sootClass.getMethodByName(args[1]);
        Body b = sootMethod.retrieveActiveBody();
        UnitGraph g = new ExceptionalUnitGraph(b);

        Formatter formatter = new Formatter();

        Flow myFlow = new Flow(g, formatter, sootClass.getName(),
                               sootMethod.getName(), tag.toString());
        formatter.print();
    }
}
