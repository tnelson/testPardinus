import kodkod.ast.*;
import kodkod.engine.*;
import kodkod.engine.config.ExtendedOptions;
import kodkod.engine.config.Options;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;
import kodkod.instance.PardinusBounds;
import kodkod.instance.Universe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Relation p = Relation.unary("P");
        Relation q = Relation.unary("Q");
        Relation r = Relation.unary("R");
        Variable x = Variable.unary("x");
        Set<Object> atoms = new HashSet<>();

        int NATOMS = 2;
        for (int i = 0; i < NATOMS; i++) {
            atoms.add("Atom"+i);
        }

        Universe u = new Universe(atoms);

        PardinusBounds pb = new PardinusBounds(u);
        pb.bound(p, u.factory().allOf(1));
        pb.bound(q, u.factory().allOf(1));
        pb.bound(r, u.factory().allOf(1));
        // Target P = all, Q = none; R has no target
        // (but target won't satisfy fmla)
        // Note targets
        pb.setTarget(p, u.factory().allOf(1));
        pb.setTarget(q, u.factory().noneOf(1));

        Formula f = p.some().and(q.some()).and(r.some());

        ExtendedOptions eo = new ExtendedOptions();
        eo.setSolver(SATFactory.PMaxSAT4J);
        eo.setSymmetryBreaking(20);
        eo.setLogTranslation(0);
        eo.setBitwidth(1); // minimal
        eo.setReporter(new TestReporter());

        // PardinusSolver doesn't implement IterableSolver,
        //  even though it provides a solveAll method.
        // We therefore break with good OOP...
        //TargetOrientedSolver<ExtendedOptions> s = new PardinusSolver(eo);
        PardinusSolver s = new PardinusSolver(eo);
        //Solution sol = s.solve(f, pb);
        Iterator<Solution> it = s.solveAll(f, pb);
        int count = 0;
        while(it.hasNext()) {
            Solution sol = it.next();
            count++;
            if(sol.sat()) {
                System.out.println(sol.instance().relationTuples());
                int pdist = NATOMS-sol.instance().relationTuples().get(p).size();
                int qdist = sol.instance().relationTuples().get(q).size();
                System.out.println("dist = "+(pdist+qdist));
            }
        }
        System.out.println("total number of instances: "+count);
    }
}
