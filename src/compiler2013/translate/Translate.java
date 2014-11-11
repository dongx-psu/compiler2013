package compiler2013.translate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import compiler2013.absyn.*;
import compiler2013.env.*;
import compiler2013.quad.*;
import compiler2013.symbol.Symbol;
import compiler2013.type.*;
import compiler2013.util.ArrDtorDealer;
import compiler2013.util.DtorDealer;
import compiler2013.util.FuncDtorDealer;
import compiler2013.util.IterLabelDealer;
import compiler2013.util.SmpDtorDealer;
import compiler2013.util.TableDealer;
import compiler2013.addr.*;

public final class Translate {
	public static int maxArgc = 0;
	public static Label runmain = new Label("run_main");
	private Env env = null;
	private Level level = null;
	private Label levlabel = null;
	
	private Stack<IterLabelDealer> iterLabels = new Stack<IterLabelDealer>();
	private List<Quad> quads = new LinkedList<Quad>();
	private LinkedList<DataFrag> datafrags = new LinkedList<DataFrag>();
	static enum RecordMark { STRUCT, UNION }
	
	
	private static Symbol symbol(String s) {
		return Symbol.getSymbol(s);
	}
	
	public Translate() {
		this(new Env(), null, null);
	}
	
	public Translate(Env e, Level lv, Label ll) {
		env = e;
		level = lv;
		levlabel = ll;
	}
	
	private void popLabel() {
		iterLabels.pop();
	}
	
	private void pushLabel(IterLabelDealer iterLabelDealer) {
		iterLabels.push(iterLabelDealer);
	}
	
	private Label breakLabel() {
		return iterLabels.peek().next;
	}
	
	private Label continueLabel() {
		return iterLabels.peek().begin;
	}
	
	private List<CompileUnit> subUnits = new LinkedList<CompileUnit>();

	private void emit(List<CompileUnit> units) {
		subUnits.addAll(units);
	}
	
	public List<CompileUnit> getUnits() {
		List<CompileUnit> allUnits = new LinkedList<CompileUnit>();
		allUnits.add(new CompileUnit(quads, level));
		allUnits.addAll(subUnits);
		return allUnits;
	}
	
	private void emit(Quad q) {
		quads.add(q);
	}
	
	private void emit(DataFrag d) {
		datafrags.add(d);
	}
	
	private void emit(LinkedList<DataFrag> dataFrags) {
		this.datafrags.addAll(dataFrags);
	}

	/*public List<Quad> getQuads() {
		return quads;
	}*/
	
	public LinkedList<DataFrag> getDataFrags() {
		return datafrags;
	}
	
	private Quad makeMove(Temp addr, Addr src) {
		if (src instanceof Const) {
			return new Move(addr, (Const)src);
		} else if (src instanceof Label) {
			return new MoveL(addr, (Label)src);
		} else if (src instanceof Temp) {
			return new Move(addr, (Temp)src);
		}
		return null;
	}
	
	private Temp makeTemp(Addr a) {
		return makeTemp(a, "");
	}
	
	private Temp makeTemp(Addr a, String c) {
		if (a instanceof Temp) {
			return (Temp) a;
		}
		else {
			Temp t = level.newLocal();
			emit(makeMove(t, a));
			return t;
		}
	}

	public void transProg(Decls d, Label l, Level v) {
		level = v;
		emit(new LABEL(l));
		transDecls(d);
		emit(new CallProc(runmain, new LinkedList<Temp>()));
	}

	private void transDecls(Decls d) {
		for (Decl now : d.list) {
			transDecl(now);
		}
	}

	private void transDecl(Decl d) {
		if (d instanceof TypeDecl) transTypeDecl((TypeDecl)d);
		else if (d instanceof VarDecl) transVarDecl((VarDecl)d);
		else if (d instanceof Func) transFunc((Func)d);
	}

	private void transTypeDecl(TypeDecl td) {
		Type t = transTy(td.type);
		LinkedList<DtorDealer> rs = transDecltors(td.decltors);
		for (DtorDealer dd: rs) {
			Symbol name = null;
			Type type = null;
			if (dd instanceof SmpDtorDealer) {
				name = dd.name;
				type = fetchPtrType(t, dd.ptrcount);
			} else if (dd instanceof ArrDtorDealer) {
				name = dd.name;
				Type tmp = fetchPtrType(t, dd.ptrcount);
				type = fetchArrType(tmp, ((ArrDtorDealer)dd).arrLength, ((ArrDtorDealer)dd).parasaddr);
			} else if (dd instanceof FuncDtorDealer) {
				name = dd.name;
				Type tmp = fetchPtrType(t, dd.ptrcount);
				type = new FUNCTION(((FuncDtorDealer) dd).agms, tmp, ((FuncDtorDealer) dd).extend);
			}
			env.types.put(name, type);
		}
	}
	
	private Type fetchArrType(Type tmp, int arrLength, List<Integer> parasaddr) {
		Type ans = tmp;
		for (int i = arrLength - 1; i >= 0; --i)
			ans = new ARRAY(ans, parasaddr.get(i).intValue());
		return ans;
	}

	private LinkedList<DtorDealer> transDecltors(Decltors decltors) {
		LinkedList<DtorDealer> ans = new LinkedList<DtorDealer>();
		for (Decltor d: decltors.list) ans.add(transDecltor(d));
		return ans;
	}
	
	private DtorDealer transDecltor(Decltor d) {
		DtorDealer now = transPlDecltor(d.pldecltor);
		if (d instanceof ArrDecltor) {
			transArrParas(((ArrDecltor)d).arrparas);
			int length = ((ArrDecltor)d).arrparas.list.size();
			now = new ArrDtorDealer(now.name, now.ptrcount, length);
			for (int i = 0; i < length; ++i) {
				int cap = ((Const)((ArrDecltor)d).arrparas.list.get(i).addr).value;
				((ArrDtorDealer)now).parasaddr.add(cap);
			}
		} else if (d instanceof FuncDecltor) {
			RECORD agm = transPara(((FuncDecltor)d).para);
			now = new FuncDtorDealer(now.name, now.ptrcount, agm, ((FuncDecltor)d).extend);
		}
		
		return now;
	}

	private RECORD transPara(Para para) {
		RECORD ans = null, temp = null;
		if (para != null) {
			for (PlDecl pd: para.list) {
				Type t = transTy(pd.ty);
				DtorDealer now = transDecltor(pd.decltor);
				Symbol name = null;
				Type type = null;
				if (now instanceof SmpDtorDealer) {
					name = now.name;
					type = fetchPtrType(t, now.ptrcount);
				} else if (now instanceof ArrDtorDealer) {
					name = now.name;
					Type tmp = fetchPtrType(t, now.ptrcount);
					type = fetchArrType(tmp, ((ArrDtorDealer)now).arrLength, ((ArrDtorDealer)now).parasaddr);
				} else if (now instanceof FuncDtorDealer) {
					name = now.name;
					Type tmp = fetchPtrType(t, now.ptrcount);
					type = new FUNCTION(((FuncDtorDealer) now).agms, tmp, ((FuncDtorDealer) now).extend);
				}
				if (ans == null) {
					temp = new RECORD(name, type, null);
					ans = temp;
				} else {
					temp.tail = new RECORD(name ,type, null);
					temp = temp.tail;
				}
			}
		}
		return ans;
	}

	private void transArrParas(ArrParas arrparas) {
		for (Expr e: arrparas.list) {
			transExpr(e);
		}
	}

	private SmpDtorDealer transPlDecltor(PlDecltor pdtor) {
		PlDecltor tmp = pdtor;
		int count = 0;
		while (tmp instanceof PtrPlDecltor) {
			++count;
			tmp = ((PtrPlDecltor)tmp).pldecltor;
		}
		return new SmpDtorDealer(((SmpPlDecltor)tmp).symbol, count); 
	}

	private Type transTy(Ty t) {
		if (t instanceof NameTy) {
			return (Type) env.types.get(((NameTy)t).symbol).value; 
		} else if (t instanceof StructTy) {
			if (((StructTy)t).structdecls == null) {
				TableDealer last = env.types.get(t.symbol);
				if (last == null) {
					TableDealer ntype = env.types.get(symbol("name " + t.symbol.toString()));
					if (ntype == null) {
						NAME tmp = new NAME(t.symbol, env.types.level);
						env.types.put(symbol("name " + t.symbol.toString()), tmp);
						return tmp;
					} else return (Type)ntype.value;
				} else return (Type)last.value;
			} else {
				STRUCT tmp = (STRUCT)buildStructDecls(((StructTy)t).structdecls, RecordMark.STRUCT);
				env.types.put(t.symbol, tmp);
				return tmp;
			}
		} else if (t instanceof UnionTy) {
			if (((UnionTy)t).structdecls == null) {
				Type last = (Type) env.types.get(t.symbol).value;
				if (last == null) {
					Type ntype = (Type) env.types.get(symbol("name " + t.symbol.toString())).value;
					if (ntype == null) {
						ntype = new NAME(t.symbol, env.types.level);
						env.types.put(symbol("name " + t.symbol.toString()), ntype);
					}
					return ntype;
				} else return last;
			} else {
				UNION tmp = (UNION)buildStructDecls(((UnionTy)t).structdecls, RecordMark.UNION);
				env.types.put(t.symbol, tmp);
				return tmp;
			}
		}
		return null;
	}

	private RECORD buildStructDecls(StructDecls structdecls, RecordMark mark) {
		RECORD re = null, tmp = null;
		for (StructDecls.StructDecl d: structdecls.list) {
			Type t =transTy(d.type);
			LinkedList<DtorDealer> rs = transDecltors(d.decltors);
			for (DtorDealer dd: rs) {
				Symbol name = null;
				Type type = null;
				if (dd instanceof SmpDtorDealer) {
					name = dd.name;
					type = fetchPtrType(t, dd.ptrcount);
				} else if (dd instanceof ArrDtorDealer) {
					name = dd.name;
					Type temp = fetchPtrType(t, dd.ptrcount);
					type = fetchArrType(temp, ((ArrDtorDealer)dd).arrLength, ((ArrDtorDealer)dd).parasaddr);
				} else if (dd instanceof FuncDtorDealer) {
					name = dd.name;
					Type temp = fetchPtrType(t, dd.ptrcount);
					type = new FUNCTION(((FuncDtorDealer) dd).agms, temp, ((FuncDtorDealer) dd).extend);
				}
				if (re == null) {
					if (mark == RecordMark.STRUCT) tmp = new STRUCT(name, type, null);
					else if (mark == RecordMark.UNION) tmp = new UNION(name, type, null);
					re = tmp;
				} else {
					if (mark == RecordMark.STRUCT) tmp.tail = new STRUCT(name ,type, null);
					else if (mark == RecordMark.UNION) tmp.tail = new UNION(name, type, null);
					tmp = tmp.tail;
				}
			}
		}
		return re;
	}
	
	private Type fetchPtrType(Type t, int ptrcount) {
		for (int i = 0; i < ptrcount; ++i)
			t = new POINTER(t);
		return t;
	}

	private void transVarDecl(VarDecl td) {
		Type t = transTy(td.type);
		if (td.initdecltors != null) {
			for (InitDecltor id: td.initdecltors.list) {
				DtorDealer now = transDecltor(id.decltor);
				Symbol name = null;
				Type type = null;
				Temp addr = null;
				if (now instanceof SmpDtorDealer) {
					name = now.name;
					type = fetchPtrType(t, now.ptrcount);
					if (type instanceof RECORD) {
						addr = level.newLocal();
						Temp size = makeTemp(new Const(type.size()));
						emit(new CallFunc(new Label("malloc"), tempList(size), addr));
					} else if (type instanceof ARRAY) {
						addr = transArrayDecl((ARRAY)type);
					} else addr = level.newLocal();
				} else if (now instanceof ArrDtorDealer) {
					name = now.name;
					Type tmp = fetchPtrType(t, now.ptrcount);
					type = fetchArrType(tmp, ((ArrDtorDealer)now).arrLength, ((ArrDtorDealer)now).parasaddr);
					addr = transArrayDecl((ARRAY)type);
				} else if (now instanceof FuncDtorDealer) {
					name = now.name;
					Type tmp = fetchPtrType(t, now.ptrcount);
					type = new FUNCTION(((FuncDtorDealer) now).agms, tmp, ((FuncDtorDealer) now).extend);
					addr = level.newLocal();
				}
				id.varAddr = addr;
				env.vars.put(name, new VarEntry(type, id.varAddr));
				transIniter(id.initer, type, id.varAddr, true);
			}
		}
	}
	
	private Temp transArrayDecl(ARRAY type) {
		Temp ans = level.newLocal();
		Temp size = makeTemp(new Const(type.size()));
		emit(new CallFunc(new Label("malloc"), tempList(size), makeTemp(ans)));
		return ans;
	}

	private List<Temp> tempList(Temp... temps) {
		List<Temp> list = new LinkedList<Temp>();
		for (Temp t : temps) {
			list.add(t);
		}
		return list;
	}

	private void transIniter(Initer initer, Type t, Temp dst, boolean flag) {
		if (initer != null) {
			if (initer instanceof PlIniter){
				transExpr(((PlIniter)initer).aexpr);
				if (flag) {
					if (((PlIniter)initer).aexpr instanceof StringConst)
						dst.strval = ((StringConst)((PlIniter)initer).aexpr).value;
					if (t instanceof ARRAY && Type.CHAR.eq(((POINTER)t).elementType)) {
						Temp tt = level.newLocal();
						Temp len = level.newLocal();
						emit(new MoveL(tt, ((Label)((PlIniter)initer).aexpr.addr)));
						emit(new CallFunc(new Label("strlen"), tempList(tt), len));
						List<Temp> tl = tempList(dst, tt, len);
						emit(new CallProc(new Label("strcpy"), tl));
					} else emit(makeMove(dst, ((PlIniter)initer).aexpr.addr));
				} else {
					if (Type.CHAR.eq(t)) emit(new StoreB(dst, new Const(0), makeTemp(((PlIniter)initer).aexpr.addr)));
					else emit(new Store(dst, new Const(0), makeTemp(((PlIniter)initer).aexpr.addr)));
				}
			} else if (initer instanceof CpdIniter) {
				for (Initer i: ((CpdIniter)initer).initers.list) {
					transIniter(i, t, dst, false);
					if (t instanceof ARRAY)
						emit(makeBinop(dst, 0, dst, new Const(((ARRAY)t).elementType.size())));
					else System.out.println("ERROR");
				}
			}
		}
	}

	private void transFunc(Func f) {
		Type t = transTy(f.typespec);
		SmpDtorDealer now = transPlDecltor(f.pldector);
		RECORD agm = transPara(f.para);
		Type rt = fetchPtrType(t, now.ptrcount);
		Label l = null;
		if (now.name == symbol("main"))
			l = runmain;
		else l = Label.forFunction(now.name);
		Type type = new FUNCTION(agm, rt, f.extend, l);
		Level newlv = new Level(level);
		env.vars.put(now.name, new VarEntry(type, newlv));
		
		transCpdStmt(f.body, agm, rt, newlv, ((FUNCTION)type).label);
	}
	
	private void transStmt(Stmt s) {
		if (s instanceof ExprStmt) transExprStmt((ExprStmt)s);
		else if (s instanceof CpdStmt) transCpdStmt((CpdStmt)s);
		else if (s instanceof SelStmt) transSelStmt((SelStmt)s);
		else if (s instanceof IterStmt) transIterStmt((IterStmt)s);
		else if (s instanceof JmpStmt) transJmpStmt((JmpStmt)s);
	}

	private void transExprStmt(ExprStmt s) {
		if (s.expr != null) transExpr(s.expr);
	}
	
	private void transDeclar(Declar d) {
		if (d instanceof TypeDecl) transTypeDecl((TypeDecl)d);
		else if (d instanceof VarDecl) transVarDecl((VarDecl)d);
	}
	
	private void transCpdStmt(CpdStmt s) {
		env.beginScope();
		
		if (s.ds != null) {
			for (Declar d: s.ds.list) transDeclar(d);
		}
		if (s.sts != null) {
			for (Stmt st: s.sts.list) transStmt(st);
		}

		env.endScope();
	}
	
	private void transCpdStmt(CpdStmt s, RECORD agm, Type rt, Level lv, Label l) {
		env.beginScope();
		
		LinkedList<Temp> params = new LinkedList<Temp>();
		
		while (agm != null) {
			Temp t = lv.newLocal();
			env.vars.put(agm.first.fieldName, new VarEntry(agm.first.type, t));
			params.add(t);
			agm = agm.tail;
		}
		
		Label levlabel = new Label(l.toString()+"_leave");
		Translate newtrans = new Translate(env, lv, levlabel);
		newtrans.emit(new LABEL(l));
		newtrans.emit(new Enter(l, lv, params));
		if (s.ds != null) {
			for (Declar d: s.ds.list) newtrans.transDeclar(d);
		}
		if (s.sts != null) {
			for (Stmt st: s.sts.list) newtrans.transStmt(st);
		}
		newtrans.emit(new LABEL(levlabel));
		newtrans.emit(new Leave(l, lv));
		
		emit(newtrans.getUnits());
		emit(newtrans.getDataFrags());
		env.endScope();
	}

	private void transSelStmt(SelStmt s) {
		Expr cond = s.cond;
		Stmt act1 = s.act1;
		Stmt act2 = s.act2;
		
		if (act2 == null) {
			Label next = new Label();
			transExpr(cond);
			emit(new IfFalse(makeTemp(cond.addr), next));
			transStmt(act1);
			emit(new LABEL(next));
		} else {
			Label next = new Label();
			Label otherwise = new Label();
			transExpr(cond);
			emit(new IfFalse(makeTemp(cond.addr), otherwise));
			transStmt(act1);
			emit(new Goto(next));
			emit(new LABEL(otherwise));
			transStmt(act2);
			emit(new LABEL(next));
		}
	}

	private void transIterStmt(IterStmt s) {
		if (s instanceof ForStmt) transForStmt((ForStmt)s);
		else if (s instanceof WhileStmt) transWhileStmt((WhileStmt)s);
	}

	private void transForStmt(ForStmt s) {
		Expr init = s.expra;
		Expr cond = s.exprb;
		Expr iter = s.exprc;
		Stmt body = s.stmt;
		
		transExpr(init);
		Label begin = new Label();
		Label next = new Label();
		emit(new LABEL(begin));
		transExpr(cond);
		emit(new IfFalse(makeTemp(cond.addr), next));
		pushLabel(new IterLabelDealer(begin, next));
		transStmt(body);
		transExpr(iter);
		popLabel();
		emit(new Goto(begin));
		emit(new LABEL(next));
	}
	
	private void transWhileStmt(WhileStmt s) {
		Label begin = new Label();
		Label next = new Label();
		
		emit(new LABEL(begin));
		transExpr(s.cond);
		emit(new IfFalse(makeTemp(s.cond.addr), next));
		pushLabel(new IterLabelDealer(begin, next));
		transStmt(s.body);
		popLabel();
		emit(new Goto(begin));
		emit(new LABEL(next));
	}

	private void transJmpStmt(JmpStmt s) {
		if (s instanceof BreakStmt) transBreakStmt((BreakStmt)s);
		else if (s instanceof ContinueStmt) transContinueStmt((ContinueStmt)s);
		else if (s instanceof ReturnStmt) transReturnStmt((ReturnStmt)s);
	}
	
	private void transBreakStmt(BreakStmt s) {
		emit(new Goto(breakLabel()));
	}
	
	private void transContinueStmt(ContinueStmt s) {
		emit(new Goto(continueLabel()));
	}

	private void transReturnStmt(ReturnStmt st) {
		transExpr(st.rtne);
		emit(new Return(makeTemp(st.rtne.addr)));
		emit(new Goto(this.levlabel));
	}

	private void transExpr(Expr e) {
		if (e instanceof AccExpr) transAccExpr((AccExpr)e);
		else if (e instanceof AdrsExpr) transAdrsExpr((AdrsExpr)e);
		else if (e instanceof CallExpr) transCallExpr((CallExpr)e);
		else if (e instanceof CastExpr) transCastExpr((CastExpr)e);
		else if (e instanceof CharConst) transCharConst((CharConst)e);
		else if (e instanceof Exprs) transExprs((Exprs)e);
		else if (e instanceof Num) transNum((Num)e);
		else if (e instanceof Op) transOp((Op)e);
		else if (e instanceof PostfixExpr) transPostfixExpr((PostfixExpr)e);
		else if (e instanceof StringConst) transStringConst((StringConst)e);
		else if (e instanceof TypeSizeExpr) transTypeSizeExpr((TypeSizeExpr)e);
		else if (e instanceof UnaryExpr) transUnaryExpr((UnaryExpr)e);
		else if (e instanceof Var) transVar((Var)e);
	}
	
	private Quad makeBinop(Temp dst, int oper, Addr src1, Addr src2) {
		Temp left = makeTemp(src1);
		if (src1 instanceof Const && src2 instanceof Const && oper <= 9) {
			int a = ((Const) src1).value, b = ((Const) src2).value;
			if (!(oper == 3 && b == 0)) {
				return new Move(dst, new Const(calculateBinop(oper, a, b)));
			}
		}
		if (src2 instanceof Const) {
			return makeBinopI(dst, oper, left, (Const)src2);
		}
		
		Temp right = makeTemp(src2);
		return new Binop(dst, oper, left, right);
	}
	
	private int calculateBinop(int oper, int a, int b) {
		if (oper == 0) return a + b;
		else if (oper == 1) return a - b;
		else if (oper == 2) return a * b;
		else if (oper == 3) return a / b;
		else if (oper == 4) return a % b;
		else if (oper == 5) return a & b;
		else if (oper == 6) return a | b;
		else if (oper == 7) return a ^ b;
		else if (oper == 8) return a << b;
		else if (oper == 9) return a >> b;
		return 0;
	}

	private Quad makeBinopI(Temp dst, int oper, Temp left, Const right) {
		return new Binop(dst, oper, left, right);
	}

	private void transAccExpr(AccExpr e) {
		transExpr(e.expr);
		e.addr = level.newLocal();
		emit(makeMove(makeTemp(e.addr), e.expr.addr));
		if (e.atype == AccExpr.AccType.INC) {
			emit(makeBinop(makeTemp(e.expr.addr), 0, e.expr.addr, new Const(1)));
			if (checkIncDec(e)) {
				Expr x = e.expr;
				if (x instanceof Exprs) x = ((Exprs)x).list.getFirst();
				transAssign(x, e.expr.addr);
			}
		} else {
			emit(makeBinop(makeTemp(e.expr.addr), 1, e.expr.addr, new Const(1)));
			if (checkIncDec(e)) {
				Expr x = e.expr;
				if (x instanceof Exprs) x = ((Exprs)x).list.getFirst();
				transAssign(x, e.expr.addr);
			}
		}
	}

	private void transAdrsExpr(AdrsExpr e) {
		e.addr = level.newLocal();
		transExpr(e.sit);
		transExpr(e.ob);

		
		if (e.type instanceof ARRAY || e.type instanceof RECORD) {
			if (e.sit.addr instanceof Const) {
				int size = ((Const)e.sit.addr).value * e.type.size();
				emit(makeBinop((Temp)e.addr, 0, makeTemp(e.ob.addr), new Const(size)));
			} else  {
				Temp base = makeTemp(e.ob.addr);
				Temp index = makeTemp(e.sit.addr);
				Temp offset = level.newLocal();
				emit(makeBinop(offset, 2, index, new Const(e.type.size())));
				emit(makeBinop((Temp)e.addr, 0, base, offset));
			}
		} else {
			if (e.sit.addr instanceof Const) {
				int size = ((Const)e.sit.addr).value * e.type.size();
				if (Type.CHAR.eq(e.type)) emit(new LoadB((Temp) e.addr, makeTemp(e.ob.addr), new Const(size)));
				else emit(new Load((Temp) e.addr, makeTemp(e.ob.addr), new Const(size)));
			} else {
				Temp base = makeTemp(e.ob.addr);
				Temp index = makeTemp(e.sit.addr);
				Temp offset = level.newLocal();
				Temp address = level.newLocal();
				emit(makeBinop(offset, 2, index, new Const(e.type.size())));
				emit(makeBinop(address, 0, base, offset));
				if (Type.CHAR.eq(e.type)) emit(new LoadB((Temp)e.addr, address, new Const(0)));
				else emit(new Load((Temp)e.addr, address, new Const(0)));
			}
		}
	}

	private void transCallExpr(CallExpr e) {
		transExpr(e.pfe);
		if (e.args != null) {
			for (Expr arg: e.args.list) {
				transExpr(arg);
			}
		}
		
		FUNCTION f = (FUNCTION)e.pfe.type;
		
		if (f.label.toString() == "printf") {
			transPrintf(e.args);
			return;
		}
		
		List<Temp> params = new LinkedList<Temp>();
		if (e.args != null) {
			for (Expr arg: e.args.list) {
				if (arg.type instanceof RECORD) {
					Temp t = level.newLocal();
					Temp size = makeTemp(new Const(arg.type.size()));
					emit(new CallFunc(new Label("malloc"), tempList(size), t));
					emit(new CallProc(new Label("memcpy"), tempList(t, (Temp)arg.addr, size)));
					params.add(t);
				} else if (arg.addr instanceof Temp)
					params.add((Temp)arg.addr);
				else {
					Temp t = level.newLocal();
					emit(makeMove(t, arg.addr));
					params.add(t);
				}
			}
		}
		
		if (params.size() > maxArgc)
			maxArgc = params.size();
		
		if (f.returnType != null && f.returnType != Type.VOID) {
			if (e.addr == null) {
				e.addr = level.newLocal();
			}
			emit(new CallFunc(f.label, params, (Temp) e.addr));
		} else {
			emit(new CallProc(f.label, params));
		}
	}

	private void transPrintf(Arguments args) {
		ArrayList<Expr> x = new ArrayList<Expr>(args.list);
		String fmt = "";
		if (x.get(0) instanceof StringConst)
			fmt = ((StringConst)(x.get(0))).value;
		else fmt = ((Temp)(x.get(0).addr)).strval;
		Label printc = new Label("printc");
		Label prints = new Label("prints");
		Label printd = new Label("printd");
		StringBuffer tmp = new StringBuffer();
		int now = 1;
		for (int i = 0; i < fmt.length(); ++i) {
			if (fmt.charAt(i) == '%') {
				if (tmp.length() > 0) {
					Label strLabel = new Label();
					emit(new DataFrag(strLabel, tmp.toString()));
					emit(new CallProc(prints, tempList(makeTemp(strLabel))));
					tmp = new StringBuffer();
				}
				i++;
				if (fmt.charAt(i) == '%') {
					emit(new CallProc(printc, tempList(makeTemp(new Const('%')))));
				} else {
					if (fmt.charAt(i) == 'd') {
						emit(new CallProc(printd, tempList(makeTemp(x.get(now).addr))));
					} else if (fmt.charAt(i) == 's') {
						emit(new CallProc(prints, tempList(makeTemp(x.get(now).addr))));
					} else if (fmt.charAt(i) == 'c') {
						emit(new CallProc(printc, tempList(makeTemp(x.get(now).addr))));
					} else if (fmt.charAt(i) == '0') {
						Temp zero = makeTemp(new Const('0'));
						Label next = new Label();
						emit(new Branch("gt", makeTemp(x.get(now).addr), new Const(1000), next));
						emit(new CallProc(printc, tempList(zero)));
						emit(new Branch("gt", makeTemp(x.get(now).addr), new Const(100), next));
						emit(new CallProc(printc, tempList(zero)));
						emit(new Branch("gt", makeTemp(x.get(now).addr), new Const(10), next));
						emit(new CallProc(printc, tempList(zero)));
						emit(new LABEL(next));
						emit(new CallProc(printd, tempList(makeTemp(x.get(now).addr))));
						i += 2;
					}
					++now;
				}
			} else  {
				tmp.append(fmt.charAt(i));
				//emit(new CallProc(printc, tempList(makeTemp(new Const(fmt.charAt(i))))));
			}
		}
		if (tmp.length() > 0) {
			Label strLabel = new Label();
			emit(new DataFrag(strLabel, tmp.toString()));
			emit(new CallProc(prints, tempList(makeTemp(strLabel))));
		}
	}

	private void transCastExpr(CastExpr e) {
		e.addr = e.expr.addr;
	}

	private Type transTypeName(TypeName type) {
		int ptrcount = 0;
		while (type instanceof PtrTypeName) {
			type = ((PtrTypeName)type).typename;
			++ptrcount;
		}
		
		Type t = transTy(((SmpTypeName)type).type);
		if (t == null) return null;
		else if (t instanceof NAME && ptrcount == 0) return null;
		
		return fetchPtrType(t, ptrcount);
	}

	private void transCharConst(CharConst e) {
		e.addr = new Const((int)e.value);
	}

	private void transExprs(Exprs e) {
		for (Expr i: e.list) {
			transExpr(i);
			e.addr = i.addr;
		}
	}

	private void transNum(Num e) {
		e.addr = new Const((int)e.value);
	}

	private Type transNameType(NAME t) {
		TableDealer now = env.types.get(t.name);
		if (now == null || t.level < now.level) return null;
		return (Type)now.value;
	}
	
	private int transOpType(Op e) {
		if (e.opType == Op.OpType.ASSIGN) return 0;
		else if (e.opType == Op.OpType.ADD_ASSIGN) return 1;
		else if (e.opType == Op.OpType.SUB_ASSIGN) return 2;
		else if (e.opType == Op.OpType.MUL_ASSIGN) return 3;
		else if (e.opType == Op.OpType.DIV_ASSIGN) return 4;
		else if (e.opType == Op.OpType.MOD_ASSIGN) return 5;
		else if (e.opType == Op.OpType.AND_ASSIGN) return 6;
		else if (e.opType == Op.OpType.OR_ASSIGN) return 7;
		else if (e.opType == Op.OpType.XOR_ASSIGN) return 8;
		else if (e.opType == Op.OpType.SHL_ASSIGN) return 9;
		else if (e.opType == Op.OpType.SHR_ASSIGN) return 10;
		else if (e.opType == Op.OpType.PLUS) return 11;
		else if (e.opType == Op.OpType.MINUS) return 12;
		else if (e.opType == Op.OpType.TIMES) return 13;
		else if (e.opType == Op.OpType.DIVIDE) return 14;
		else if (e.opType == Op.OpType.MOD) return 15;
		else if (e.opType == Op.OpType.AND) return 16;
		else if (e.opType == Op.OpType.OR) return 17;
		else if (e.opType == Op.OpType.XOR) return 18;
		else if (e.opType == Op.OpType.SHL) return 19;
		else if (e.opType == Op.OpType.SHR) return 20;
		else if (e.opType == Op.OpType.PARAAND) return 21;
		else if (e.opType == Op.OpType.PARAOR) return 22;
		else if (e.opType == Op.OpType.EQ) return 23;
		else if (e.opType == Op.OpType.NE) return 24;
		else if (e.opType == Op.OpType.LT) return 25;
		else if (e.opType == Op.OpType.LE) return 26;
		else if (e.opType == Op.OpType.GT) return 27;
		else if (e.opType == Op.OpType.GE) return 28;
		return 0;
	}
	
	private void transAssign(Expr left, Addr right) {
		if (left instanceof AdrsExpr) {
			AdrsExpr var = (AdrsExpr) left;
			transExpr(var.ob);
			transExpr(var.sit);
			if (var.sit.addr instanceof Const) {
				Temp expTemp = makeTemp(right, "assignAdrsExpr");
				int offset = ((Const)var.sit.addr).value * left.type.size();
				if (Type.CHAR.eq(left.type)) emit(new StoreB(makeTemp(var.ob.addr), new Const(offset), expTemp));
				else emit(new Store(makeTemp(var.ob.addr), new Const(offset), expTemp));
			} else {
				Temp address = level.newLocal();
				Temp offset = level.newLocal();
				Temp base = makeTemp(var.ob.addr);
				Temp index = makeTemp(var.sit.addr);
				emit(makeBinop(offset, 2, index, new Const(left.type.size())));
				emit(makeBinop(address, 0, base, offset));
				if (Type.CHAR.eq(left.type)) emit(new StoreB(address, new Const(0), makeTemp(right, "[]=")));
				else emit(new Store(address, new Const(0), makeTemp(right, "[]=")));
			}
		} else if (left instanceof PostfixExpr) {
			PostfixExpr pe = (PostfixExpr) left;
			transExpr(pe.pfe);
			Temp now = (Temp)pe.pfe.addr;
			Type t = pe.pfe.type;
			if (t instanceof NAME) t = transNameType((NAME)t);
			if (pe.pt == PostfixExpr.PfType.PTR) {
				now = level.newLocal();
				emit(makeUnaryop(now, 1, pe.pfe.addr));
				t = ((POINTER)t).elementType;
			}
			int offset = ((RECORD)t).getFieldIndex(pe.symbol);
			if (Type.CHAR.eq(left.type)) emit(new StoreB(now, new Const(offset), makeTemp(right)));
			else emit(new Store(now, new Const(offset), makeTemp(right)));
		} else if (left instanceof UnaryExpr && ((UnaryExpr)left).utype == UnaryExpr.UnaryType.TIMES) {
			UnaryExpr ue = (UnaryExpr)left;
			transExpr(ue.expr);
			if (Type.CHAR.eq(left.type)) emit(new StoreB(makeTemp(ue.expr.addr), new Const(0), makeTemp(right)));
			else emit(new Store(makeTemp(ue.expr.addr), new Const(0), makeTemp(right)));
		} else if (left.type instanceof RECORD) {
			transExpr(left);
			Temp size = makeTemp(new Const(left.type.size()));
			assert(left.addr instanceof Temp);
			assert(right instanceof Temp);
			emit(new CallProc(new Label("memcpy"), tempList((Temp)left.addr, (Temp)right, size)));
		} else {
			transExpr(left);
			assert(left.addr instanceof Temp);
			emit(makeMove((Temp)left.addr, right));
		}
	}
	
	private Quad makeUnaryop(Temp dst, int oper, Addr src) {
		if (src instanceof Const && oper >=2 && oper <= 5) {
			return new Move(dst, new Const(calUop(oper, ((Const)src).value)));
		} else {
			return new Unaryop(dst, oper, makeTemp(src));
		}
	}

	private int calUop(int oper, int a) {
		if (oper == 2) {
			if (a == 0) return 0;
			else return 1;
		} else if (oper == 3) {
			return ~a;
		} else if (oper == 4) {
			return +a;
		} else if (oper == 5) {
			return -a;
		}
		return 0;
	}

	private void transOp(Op e) {
		int opty = transOpType(e);
		if (opty == 0) {
			transExpr(e.right);
			if (!(e.left instanceof AdrsExpr || e.left instanceof PostfixExpr)
				&& e.left.addr instanceof Temp && e.right instanceof CallExpr)
				e.right.addr = e.left.addr;
			transAssign(e.left, e.right.addr);
			e.addr = e.right.addr;
		} else if (opty <= 10) {
			transExpr(e.left);
			transExpr(e.right);
			Temp tmp = level.newLocal();
			emit(makeBinop(tmp, opty - 1, e.left.addr, e.right.addr));
			transAssign(e.left, tmp);
			e.addr = e.left.addr;
		} else if (opty == 21) {
			e.addr = level.newLocal();
			Label next = new Label();
			Label otherwise = new Label();
			if (!(e.left instanceof Op && transOpType((Op)(e.left)) == 21)) transExpr(e.left);
			else transParaAndOp((Op)(e.left), otherwise);
			emit(new IfFalse(makeTemp(e.left.addr), otherwise));
			transExpr(e.right);
			emit(makeBinop((Temp)e.addr, 13, e.right.addr, new Const(0)));
			emit(new Goto(next));
			emit(new LABEL(otherwise));
			emit(makeMove((Temp)e.addr, new Const(0)));
			emit(new LABEL(next));
			
		} else if (opty == 22) {
			e.addr = level.newLocal();
			Label next = new Label();
			Label otherwise = new Label();
			Label fall = new Label();
			if (!(e.left instanceof Op && transOpType((Op)(e.left)) == 22)) transExpr(e.left);
			else transParaOrOp((Op)(e.left), fall);
			emit(new IfFalse(makeTemp(e.left.addr), otherwise));
			emit(new LABEL(fall));
			emit(makeMove((Temp)e.addr, new Const(1)));
			emit(new Goto(next));
			emit(new LABEL(otherwise));
			transExpr(e.right);
			emit(makeBinop((Temp)e.addr, 13, e.right.addr, new Const(0)));
			emit(new LABEL(next));
		} else {
			e.addr = level.newLocal();
			transExpr(e.left);
			transExpr(e.right);
			emit(makeBinop((Temp)e.addr, opty - 11, e.left.addr, e.right.addr));
		}
	}
	
	private void transParaAndOp(Op e, Label fall) {
		e.addr = level.newLocal();
		if (!(e.left instanceof Op && transOpType((Op)(e.left)) == 21)) transExpr(e.left);
		else transParaAndOp((Op)(e.left), fall);
		emit(new IfFalse(makeTemp(e.left.addr), fall));
		transExpr(e.right);
		emit(makeBinop((Temp)e.addr, 13, e.right.addr, new Const(0)));
	}
	
	private void transParaOrOp(Op e, Label fall) {
		e.addr = level.newLocal();
		Label otherwise = new Label();
		if (!(e.left instanceof Op && transOpType((Op)(e.left)) == 22)) transExpr(e.left);
		else transParaOrOp((Op)(e.left), fall);
		emit(new IfFalse(makeTemp(e.left.addr), otherwise));
		emit(new Goto(fall));
		emit(new LABEL(otherwise));
		transExpr(e.right);
		emit(makeBinop((Temp)e.addr, 13, e.right.addr, new Const(0)));
	}

	private void transPostfixExpr(PostfixExpr pe) {
		transExpr(pe.pfe);
		Temp now = (Temp)pe.pfe.addr;
		Type t = pe.pfe.type;
		if (t instanceof NAME) t = transNameType((NAME)t);
		if (pe.pt == PostfixExpr.PfType.PTR) {
			//now = level.newLocal();
			//emit(makeUnaryop(now, 1, pe.pfe.addr));
			t = ((POINTER)t).elementType;
		}
		int offset = ((RECORD)t).getFieldIndex(pe.symbol);
		pe.addr = level.newLocal();
		if (Type.CHAR.eq(pe.type))
			emit(new LoadB((Temp)pe.addr, now, new Const(offset)));
		else if (!(pe.type instanceof ARRAY || pe.type instanceof RECORD))
			emit(new Load((Temp)pe.addr, now, new Const(offset)));
		else emit(makeBinop((Temp)pe.addr, 0, now, new Const(offset)));
	}

	private void transStringConst(StringConst e) {
		Label strLabel = new Label();
		e.addr = strLabel;
		emit(new DataFrag(strLabel, e.value));
	}

	private void transTypeSizeExpr(TypeSizeExpr e) {
		Type t = transTypeName(e.ty);
		e.addr = new Const(t.size());
	}

	private void transUnaryExpr(UnaryExpr e) {
		transExpr(e.expr);
		if (e.utype == UnaryExpr.UnaryType.AND) {
			if (e.expr.type instanceof RECORD || e.expr.type instanceof ARRAY)
				e.addr = e.expr.addr;
			else {
				e.addr = level.newLocal();
				emit(makeUnaryop((Temp)e.addr, 0, e.expr.addr));
			}
		} else if (e.utype == UnaryExpr.UnaryType.INC) {
			emit(makeBinop(makeTemp(e.expr.addr), 0, e.expr.addr, new Const(1)));
			e.addr = e.expr.addr;
			if (checkIncDec(e)) {
				Expr x = e.expr;
				if (x instanceof Exprs) x = ((Exprs)x).list.getFirst();
				transAssign(x, e.expr.addr);
			}
		} else if (e.utype == UnaryExpr.UnaryType.DEC) {
			emit(makeBinop(makeTemp(e.expr.addr), 1, e.expr.addr, new Const(1)));
			e.addr = e.expr.addr;
			if (checkIncDec(e)) {
				Expr x = e.expr;
				if (x instanceof Exprs) x = ((Exprs)x).list.getFirst();
				transAssign(x, e.expr.addr);
			}
		} else if (e.utype == UnaryExpr.UnaryType.PLUS) {
			e.addr = level.newLocal();
			emit(makeUnaryop(makeTemp(e.addr), 4, e.expr.addr));
		} else if (e.utype == UnaryExpr.UnaryType.MINUS) {
			e.addr = level.newLocal();
			emit(makeUnaryop(makeTemp(e.addr), 5, e.expr.addr));
		} else if (e.utype == UnaryExpr.UnaryType.TIDLE) {
			e.addr = level.newLocal();
			emit(makeUnaryop(makeTemp(e.addr), 3, e.expr.addr));
		} else if (e.utype == UnaryExpr.UnaryType.NOT) {
			e.addr = level.newLocal();
			Label next = new Label();
			Label otherwise = new Label();
			transExpr(e.expr);
			emit(new Branch("ne", makeTemp(e.expr.addr), new Const(0), otherwise));
			emit(makeMove((Temp)e.addr, new Const(1)));
			emit(new LABEL(otherwise));
			emit(makeMove((Temp)e.addr, new Const(0)));
			emit(new LABEL(next));
		} else if (e.utype == UnaryExpr.UnaryType.SIZEOF) {
			Type t = e.expr.type;
			e.addr = new Const(t.size());
		} else if (e.utype == UnaryExpr.UnaryType.TIMES) {
			if (e.expr.type instanceof RECORD || e.expr.type instanceof ARRAY)
				e.addr = e.expr.addr;
			else {
				e.addr = level.newLocal();
				emit(makeUnaryop(makeTemp(e.addr), 1, e.expr.addr));
			}
		}
	}

	private boolean checkIncDec(Expr e) {
		Expr x = null;
		if (e instanceof UnaryExpr) x = ((UnaryExpr)e).expr;
		else if (e instanceof AccExpr) x = ((AccExpr)e).expr;
		if (x instanceof Exprs) x = ((Exprs)x).list.getFirst();
		if (x instanceof AdrsExpr || x instanceof PostfixExpr || x instanceof UnaryExpr)
			return true;
		return false;
	}

	private void transVar(Var e) {
		VarEntry x = (VarEntry)env.vars.get(e.symbol).value;
		e.addr = x.varAddr;
	}
}
