/*
  Experimenting with Pardinus
  Tim Nelson, August 2020

  Sets up a target-oriented model-finding problem
  with 2 atoms and 3 unary relations: p, q, and r.

  Target for p: both atoms.
  Target for q: no atoms.
  Target for r: (absent).

  Constraint: all 3 relations are non-empty.
 */

import kodkod.ast.*;
import kodkod.engine.*;
import kodkod.engine.config.ExtendedOptions;
import kodkod.engine.config.TargetOptions;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.*;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Relation p = Relation.unary("P");
        Relation q = Relation.unary("Q");
        Relation r = Relation.unary("R");

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
        pb.setTarget(p, u.factory().allOf(1));
        pb.setTarget(q, u.factory().noneOf(1));
        System.out.println("target for p: "+pb.target(p));
        System.out.println("target for q: "+pb.target(q));
        System.out.println("target for r: "+pb.target(r));

        Formula f = p.some().and(q.some()).and(r.some());
        System.out.println("formula = "+f);

        ///////////////////////////////////////////////////

        ExtendedOptions eo = new ExtendedOptions();
        eo.setSolver(SATFactory.PMaxSAT4J);
        eo.setSymmetryBreaking(20);
        eo.setLogTranslation(0);
        eo.setBitwidth(1); // minimal

        // Note target mode: I expected FAR to begin
        // with something further away from the target.
        eo.setTargetMode(TargetOptions.TMode.FAR);

        eo.setConfigOptions(eo); // TN TODO: this seems needed?
        eo.setReporter(new TestReporter());

        // Break with good interface use
        // TN TODO: check if Aug 2020 update made this unnecessary?
        PardinusSolver s = new PardinusSolver(eo);

        ///////////////////////////////////////////////////

        // TN note: new "Explorer" iterator.
        Explorer<Solution> sols =  s.solveAll(f, pb);
        System.out.println("solver target mode: "+s.options().targetMode());
        int count = 0;
        Solution lastSol = null;
        while(sols.hasNext()) {
            Solution sol = sols.next();
            count++;
            if(sol.sat()) {
                System.out.println("-------------------");
                System.out.println(sol.instance().relationTuples());
                System.out.println("dist from target = "+computeDist(pb, sol.instance()));
                if(lastSol != null)
                    System.out.println("dist from prior soln = "+computeDist(pb, lastSol.instance(), sol.instance()));
            }
            lastSol = sol;
        }
        System.out.println("total number of instances: "+count);
    }

    /**
     * Compute Hamming dist between target and instance
     * Relations not in target aren't counted.
     *
     * @param pb
     * @param instance
     * @return
     */
    private static int computeDist(PardinusBounds pb, Instance instance) {
        int counter = 0;
        for(Relation r : pb.targets().keySet()) {
            for(Tuple t : pb.target(r)) {
                if(!instance.tuples(r).contains(t))
                    counter++;
            }
            for(Tuple t : instance.tuples(r)) {
                if(!pb.target(r).contains(t))
                    counter++;
            }
        }
        return counter;
    }

    private static int computeDist(PardinusBounds pb, Instance old, Instance instance) {
        int counter = 0;
        for(Relation r : old.relations()) {
            if(pb.targets().keySet().contains(r)) {
                for (Tuple t : old.tuples(r)) {
                    if (!instance.tuples(r).contains(t))
                        counter++;
                }
                for (Tuple t : instance.tuples(r)) {
                    if (!old.tuples(r).contains(t))
                        counter++;
                }
            }
        }
        return counter;
    }
}
