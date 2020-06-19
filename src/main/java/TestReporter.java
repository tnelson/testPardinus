import kodkod.ast.Decl;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.bool.BooleanFormula;
import kodkod.engine.config.Reporter;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.util.ints.IntSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestReporter implements Reporter {

    @Override
    public void detectingSymmetries(Bounds bounds) {

    }

    @Override
    public void detectedSymmetries(Set<IntSet> set) {
        System.out.println("Detected symmetries: "+set);
    }

    @Override
    public void optimizingBoundsAndFormula() {

    }

    @Override
    public void skolemizing(Decl decl, Relation relation, List<Decl> list) {

    }

    @Override
    public void translatingToBoolean(Formula formula, Bounds bounds) {

    }

    @Override
    public void generatingSBP() {

    }

    @Override
    public void translatingToCNF(BooleanFormula booleanFormula) {

    }

    @Override
    public void solvingCNF(int i, int i1, int i2) {

    }

    @Override
    public void reportLex(List<Map.Entry<Relation, Tuple>> list, List<Map.Entry<Relation, Tuple>> list1) {

    }

    @Override
    public void debug(String s) {
        System.out.println("Debug: "+s);
    }

    @Override
    public void warning(String s) {
        System.out.println("Warning: "+s);
    }

    @Override
    public void reportConfigs(int i, int i1, int i2, int i3) {

    }
}
