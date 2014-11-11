package compiler2013.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import compiler2013.quad.Quad;
import compiler2013.analysis.*;

public class CompileUnit {
	private List<Quad> quads = null;
	private Level level = null;
	private LinkedList<BasicBlock> blocks = null;
	private ArrayList<LiveInterval> intervals;

	public CompileUnit(List<Quad> quads, Level level) {
		this.quads = quads;
		this.level = level;
	}

	public List<Quad> getQuads() {
		return quads;
	}
	
	public void setQuads(List<Quad> quads) {
		if (!(quads instanceof LinkedList)) {
			quads = new LinkedList<Quad>(quads);
		}
		this.quads = quads;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void replaceBranches(Analyzer analyzer) {
		quads = analyzer.replaceBranchs(quads);
	}
	
	public void findBasicBlocks(Analyzer analyzer) {
		blocks = analyzer.getBasicBlocks(quads);
	}
	
	public void findLiveness(Analyzer analyzer) {
		analyzer.findLiveness(blocks);
	}
	
	public LinkedList<BasicBlock> getBasicBlocks() {
		return blocks;
	}
	
	public void findLiveIntervals(Analyzer analyzer) {
		intervals = new ArrayList<LiveInterval>(analyzer.findLiveIntervals(quads).values());
		Collections.sort(intervals);
	}
	
	public ArrayList<LiveInterval> getLiveIntervals() {
		return intervals;
	}
}
