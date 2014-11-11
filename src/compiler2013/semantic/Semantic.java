package compiler2013.semantic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import compiler2013.absyn.*;
import compiler2013.addr.Label;
import compiler2013.env.*;
import compiler2013.symbol.Symbol;
import compiler2013.translate.Translate;
import compiler2013.type.*;
import compiler2013.util.*;
import compiler2013.util.Error;

public class Semantic {
	private Env env = null;
	private List<Error> errors = new LinkedList<Error>();
	static enum RecordMark { STRUCT, UNION }
	public Type returnType;
	private int loopCount = 0;
	
	private static Symbol symbol(String s) {
		return Symbol.getSymbol(s);
	}
	
	private void putType(Symbol name, Type t) {
		TableDealer tmp = env.types.get(name);
		if (!(tmp == null) && (tmp.level == env.types.level))
			error("Redeclaration on type symbol: " + name);
		else env.types.put(name, t);
	}
	
	private void putVar(Symbol name, VarEntry v) {
		TableDealer tmp = env.vars.get(name);
		if (!(tmp == null) && (tmp.level == env.vars.level))
			error("Redeclaration on var symbol: " + name);
		else env.vars.put(name, v);
	}
	
	private Type checkNameType(NAME t) {
		TableDealer now = env.types.get(t.name);
		if (now == null || t.level < now.level) return null;
		return (Type)now.value;
	}
	
	public Semantic() {
		this(new Env(), Type.VOID);
	}
	
	public Semantic(Env e, Type t) {
		env = e;
		returnType = t;
	}
	
	private void error(String message) {
		errors.add(new Error(message, true));
	}
	
	private void fatalError(String message) {
		error(message);
		printErrors();
		new Exception().printStackTrace();
	}
	
	public boolean hasError() {
		return errors.size() > 0;
	}
	
	public void printErrors() {
		for (Error e : errors) {
			System.err.println(e);
		}
	}
	
	private void flushErrorsTo(Semantic another) {
		for (Error e : errors) {
			another.errors.add(e);
		}
	}
	
	public void checkProg(Decls d) {
		loopCount = 0;
		checkDecls(d);
	}
	
	private void checkDecls(Decls d) {
		for (Decl now : d.list) {
			checkDecl(now);
		}
	}
	
	private void checkDecl(Decl d) {
		if (d instanceof TypeDecl) checkTypeDecl((TypeDecl)d);
		else if (d instanceof VarDecl) checkVarDecl((VarDecl)d);
		else if (d instanceof Func) checkFunc((Func)d);
		else fatalError("transDecl");
	}
	
	private void checkTypeDecl(TypeDecl td) {
		Type t = checkTy(td.type);
		if (t == null) {
			error("Type not found in structdecls");
			return;
		}
		LinkedList<DtorDealer> rs = checkDecltors(td.decltors);
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
			if (type instanceof NAME) {
				error("Name type definition imcomplete");
				return;
			}
			putType(name, type);
		}
	}
	
	private Type fetchArrType(Type tmp, int arrLength, List<Integer> parasaddr) {
		Type ans = tmp;
		for (int i = arrLength - 1; i >= 0; --i)
			ans = new ARRAY(ans, parasaddr.get(i).intValue());
		return ans;
	}

	private LinkedList<DtorDealer> checkDecltors(Decltors decltors) {
		LinkedList<DtorDealer> ans = new LinkedList<DtorDealer>();
		for (Decltor d: decltors.list) ans.add(checkDecltor(d));
		return ans;
	}
	
	private DtorDealer checkDecltor(Decltor d) {
		DtorDealer now = checkPlDecltor(d.pldecltor);
		if (d instanceof ArrDecltor) {
			checkArrParas(((ArrDecltor)d).arrparas);
			int length = ((ArrDecltor)d).arrparas.list.size();
			now = new ArrDtorDealer(now.name, now.ptrcount, length);
			for (int i = 0; i < length; ++i) {
				int cap = ((Num)((ArrDecltor)d).arrparas.list.get(i)).value;
				((ArrDtorDealer)now).parasaddr.add(cap);
			}
		} else if (d instanceof FuncDecltor) {
			RECORD agm = checkPara(((FuncDecltor)d).para);
			now = new FuncDtorDealer(now.name, now.ptrcount, agm, ((FuncDecltor)d).extend);
		}
		
		return now;
	}

	private RECORD checkPara(Para para) {
		RECORD ans = null, temp = null;
		HashSet<Symbol> dict = new HashSet<Symbol>();
		if (para != null) {
			for (PlDecl pd: para.list) {
				Type t = checkTy(pd.ty);
				if (t == null) {
					error("Type not found in para.");
					return null;
				}
				DtorDealer now = checkDecltor(pd.decltor);
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
				if (type instanceof NAME) {
					error("Name type definition imcomplete");
					return null;
				}
				if (dict.contains(name)) {
					error("Redefine paras on " + name);
					return null;
				} else dict.add(name);
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

	private Type fetchPtrType(Type t, int ptrcount) {
		for (int i = 0; i < ptrcount; ++i)
			t = new POINTER(t);
		return t;
	}

	private Integer checkArrExpr(Expr e) {
		if (e instanceof Num) {
			e.type = Type.INT;
			return ((Num) e).value;
		} else if (e instanceof CharConst) {
			e.type = Type.CHAR;
			return (int)((CharConst) e).value;
		} else if (e instanceof CastExpr) {
			Type t = checkTypeName(((CastExpr) e).tn);
			if (t == null) {
				error("Type not found in CastExpr");
				return null;
			} else if (!(Type.INT.eq(t) || Type.CHAR.eq(t))) {
				error("Type must be INT or CHAR in CastExpr in ArrDeclar");
				return null;
			}
			e.type = t;
			return checkArrExpr(((CastExpr) e).expr);
		} else if (e instanceof Exprs) {
			int i;
			for (i = 0; i < ((Exprs) e).list.size() - 1; ++i)
				checkExpr(((Exprs) e).list.get(i));
			e.type = Type.INT;
			return checkArrExpr(((Exprs) e).list.get(i));
		} else if (e instanceof TypeSizeExpr) {
			Type t = checkTypeName(((TypeSizeExpr) e).ty);
			if (t == null) error("Type not found in TypeSizeExpr");
			e.type = Type.INT;
			return t.size();
		} else if (e instanceof UnaryExpr && ((UnaryExpr)e).utype == UnaryExpr.UnaryType.SIZEOF) {
			Type t = checkExpr(((UnaryExpr)e).expr);
			e.type = Type.INT;
			return t.size();
		} else if (e instanceof UnaryExpr && ((UnaryExpr)e).utype == UnaryExpr.UnaryType.NOT) {
			e.type = Type.INT;
			return (checkArrExpr(((UnaryExpr)e).expr) == 0?1:0);
		} else if (e instanceof UnaryExpr && ((UnaryExpr)e).utype == UnaryExpr.UnaryType.TIDLE) {
			e.type = Type.INT;
			return ~checkArrExpr(((UnaryExpr)e).expr);
		} else if (e instanceof UnaryExpr && ((UnaryExpr)e).utype == UnaryExpr.UnaryType.PLUS) {
			e.type = Type.INT;
			return +checkArrExpr(((UnaryExpr)e).expr);
		} else if (e instanceof UnaryExpr && ((UnaryExpr)e).utype == UnaryExpr.UnaryType.MINUS) {
			e.type = Type.INT;
			return -checkArrExpr(((UnaryExpr)e).expr);
		} else if (e instanceof Op) {
			if (((Op) e).opType == Op.OpType.PLUS) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) + checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.MINUS) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) - checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.TIMES) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) * checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.DIVIDE) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) / checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.MOD) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) % checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.AND) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) & checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.OR) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) | checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.XOR) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) ^ checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.SHL) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) << checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.SHR) {
				e.type = Type.INT;
				return checkArrExpr(((Op) e).left) >> checkArrExpr(((Op) e).right); 
			} else if (((Op) e).opType == Op.OpType.PARAAND) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) != 0 && checkArrExpr(((Op) e).right) != 0)?1:0; 
			} else if (((Op) e).opType == Op.OpType.PARAOR) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) != 0 || checkArrExpr(((Op) e).right) != 0)?1:0; 
			} else if (((Op) e).opType == Op.OpType.LT) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) < checkArrExpr(((Op) e).right))?1:0; 
			} else if (((Op) e).opType == Op.OpType.GT) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) > checkArrExpr(((Op) e).right))?1:0;
			} else if (((Op) e).opType == Op.OpType.LE) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) <= checkArrExpr(((Op) e).right))?1:0;
			} else if (((Op) e).opType == Op.OpType.GE) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) >= checkArrExpr(((Op) e).right))?1:0;
			} else if (((Op) e).opType == Op.OpType.EQ) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) == checkArrExpr(((Op) e).right))?1:0;
			} else if (((Op) e).opType == Op.OpType.NE) {
				e.type = Type.INT;
				return (checkArrExpr(((Op) e).left) != checkArrExpr(((Op) e).right))?1:0;
			}
		} else error("Error in checkArrExpr");
		return null;
	}
	
	private void checkArrParas(ArrParas arrparas) {
		for (int i = 0; i < arrparas.list.size(); ++i) {
			Integer value = checkArrExpr(arrparas.list.get(i));
			if (value == null)
				error("Error in checkArrExpr");
			arrparas.list.set(i, new Num(value));
		}
	}

	private SmpDtorDealer checkPlDecltor(PlDecltor pdtor) {
		PlDecltor tmp = pdtor;
		int count = 0;
		while (tmp instanceof PtrPlDecltor) {
			++count;
			tmp = ((PtrPlDecltor)tmp).pldecltor;
		}
		return new SmpDtorDealer(((SmpPlDecltor)tmp).symbol, count); 
	}

	private Type checkTy(Ty t) {
		if (t instanceof NameTy) {
			return (Type) env.types.get(((NameTy)t).symbol).value; 
		} else if (t instanceof StructTy) {
			if (((StructTy)t).structdecls == null) {
				TableDealer last = env.types.get(t.symbol);
				if (last == null) {
					TableDealer ntype = env.types.get(symbol("name " + t.symbol.toString()));
					if (ntype == null) {
						NAME tmp = new NAME(t.symbol, env.types.level);
						putType(symbol("name " + t.symbol.toString()), tmp);
						return tmp;
					} else return (Type)ntype.value;
				} else return (Type)last.value;
			} else {
				STRUCT tmp = (STRUCT)buildStructDecls(((StructTy)t).structdecls, RecordMark.STRUCT);
				putType(t.symbol, tmp);
				return tmp;
			}
		} else if (t instanceof UnionTy) {
			if (((UnionTy)t).structdecls == null) {
				Type last = (Type) env.types.get(t.symbol).value;
				if (last == null) {
					Type ntype = (Type) env.types.get(symbol("name " + t.symbol.toString())).value;
					if (ntype == null) {
						ntype = new NAME(t.symbol, env.types.level);
						putType(symbol("name " + t.symbol.toString()), ntype);
					}
					return ntype;
				} else return last;
			} else {
				UNION tmp = (UNION)buildStructDecls(((UnionTy)t).structdecls, RecordMark.UNION);
				putType(t.symbol, tmp);
				return tmp;
			}
		} else fatalError("transTypeSpec");
		return null;
	}

	private RECORD buildStructDecls(StructDecls structdecls, RecordMark mark) {
		RECORD re = null, tmp = null;
		HashSet<Symbol> dict = new HashSet<Symbol>();
		for (StructDecls.StructDecl d: structdecls.list) {
			Type t = checkTy(d.type);
			if (t == null) {
				error("Type not found in structdecls");
			} else {
				LinkedList<DtorDealer> rs = checkDecltors(d.decltors);
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
					if (type instanceof NAME) {
						error("Name type definition imcomplete");
						return null;
					}
					if (dict.contains(name)) {
						error("Redefine paras on " + name);
						return null;
					} else dict.add(name);
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
		}
		return re;
	}

	private void checkVarDecl(VarDecl td) {
		Type t = checkTy(td.type);
		if (t == null) error("Type not found in vardecl");
		else {
			if (td.initdecltors != null) {
				for (InitDecltor id: td.initdecltors.list) {
					DtorDealer now = checkDecltor(id.decltor);
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
					if (type instanceof NAME) {
						error("Name type definition imcomplete");
						return;
					}
					VarEntry var = new VarEntry(type);
					putVar(name, var);
					checkIniter(id.initer, type);
				}
			}
		}
	}
	
	private void checkIniter(Initer initer, Type t) {
		if (initer != null) {
			if (t instanceof FUNCTION) error("Cannot initialize function like this");
			else if (initer instanceof PlIniter){
				Type now = checkExpr(((PlIniter)initer).aexpr);
				if (!checkTypeEq(t, now)) error("Type unmatch in initer");
			} else if (initer instanceof CpdIniter) {
				for (Initer i: ((CpdIniter)initer).initers.list) checkIniter(i, t);
			}
		}
	}

	private void checkFunc(Func f) {
		Type t = checkTy(f.typespec);
		SmpDtorDealer now = checkPlDecltor(f.pldector);
		RECORD agm = checkPara(f.para);
		Type rt = fetchPtrType(t, now.ptrcount);
		if (rt instanceof NAME) {
			error("Function return type definition imcomplete");
			return;
		}
		Label l = null;
		if (now.name == symbol("main"))
			l = Translate.runmain;
		else l = Label.forFunction(now.name);
		Type type = new FUNCTION(agm, rt, f.extend, l);
		putVar(now.name, new VarEntry(type));
		checkCpdStmt(f.body, agm, rt);
	}
	
	private void checkStmt(Stmt s) {
		if (s instanceof ExprStmt) checkExprStmt((ExprStmt)s);
		else if (s instanceof CpdStmt) checkCpdStmt((CpdStmt)s);
		else if (s instanceof SelStmt) checkSelStmt((SelStmt)s);
		else if (s instanceof IterStmt) checkIterStmt((IterStmt)s);
		else if (s instanceof JmpStmt) checkJmpStmt((JmpStmt)s);
		else fatalError("transStmt");
	}

	private void checkExprStmt(ExprStmt s) {
		if (s.expr != null) checkExpr(s.expr);
	}
	
	private void checkDeclar(Declar d) {
		if (d instanceof TypeDecl) checkTypeDecl((TypeDecl)d);
		else if (d instanceof VarDecl) checkVarDecl((VarDecl)d);
		else fatalError("transDeclar");
	}
	
	private void checkCpdStmt(CpdStmt s) {
		env.beginScope();
		
		if (s.ds != null) {
			for (Declar d: s.ds.list) checkDeclar(d);
		}
		if (s.sts != null) {
			for (Stmt st: s.sts.list) checkStmt(st);
		}

		env.endScope();
	}
	
	private void checkCpdStmt(CpdStmt s, RECORD agm, Type rt) {
		env.beginScope();
		
		while (agm != null) {
			putVar(agm.first.fieldName, new VarEntry(agm.first.type));
			agm = agm.tail;
		}
		
		Semantic newsem = new Semantic(env, rt);
		if (s.ds != null) {
			for (Declar d: s.ds.list) newsem.checkDeclar(d);
		}
		if (s.sts != null) {
			for (Stmt st: s.sts.list) newsem.checkStmt(st);
		}
		
		if (newsem.hasError()) {
			newsem.flushErrorsTo(this);
		}
		
		env.endScope();
	}
	
	private void checkSelStmt(SelStmt s) {
		Type c = checkExpr(s.cond);
		if (!(Type.INT.eq(c) || Type.CHAR.eq(c) || (c instanceof POINTER)))
			error("Type must be int/char/pointer in conditions");
		checkStmt(s.act1);
		if (s.act2 != null) checkStmt(s.act2);
	}

	private void checkIterStmt(IterStmt s) {
		if (s instanceof ForStmt) checkForStmt((ForStmt)s);
		else if (s instanceof WhileStmt) checkWhileStmt((WhileStmt)s);
		else error("transIterStmt");
	}

	private void checkForStmt(ForStmt s) {
		Type c = Type.VOID;
		if (s.expra != null) checkExpr(s.expra);
		if (s.exprb != null) c = checkExpr(s.exprb);
		if (s.exprc != null) checkExpr(s.exprc);
		if (!(Type.INT.eq(c) || Type.CHAR.eq(c) || (c instanceof POINTER)))
			error("Type must be int/char/pointer in conditions");
		++loopCount;
		checkStmt(s.stmt);
		--loopCount;
	}
	
	private void checkWhileStmt(WhileStmt s) {
		Type c = checkExpr(s.cond);
		if (!(Type.INT.eq(c) || Type.CHAR.eq(c) || (c instanceof POINTER)))
			error("Type must be int/char/pointer in conditions");
		++loopCount;
		checkStmt(s.body);
		--loopCount;
	}

	private void checkJmpStmt(JmpStmt s) {
		if (s instanceof BreakStmt) checkBreakStmt((BreakStmt)s);
		else if (s instanceof ContinueStmt) checkContinueStmt((ContinueStmt)s);
		else if (s instanceof ReturnStmt) checkReturnStmt((ReturnStmt)s);
	}
	
	private void checkBreakStmt(BreakStmt s) {
		if (loopCount == 0) error("break must be in a loop");
	}
	
	private void checkContinueStmt(ContinueStmt s) {
		if (loopCount == 0) error("continue must be in a loop");
	}

	private void checkReturnStmt(ReturnStmt st) {
		Type rt = null;
		if (st.rtne != null) rt = checkExpr(st.rtne);
		if (st.rtne == null && !Type.VOID.eq(returnType)) error("Return type isn't void");
		else if (st.rtne != null && Type.VOID.eq(returnType)) error("Return type is void, cannot return things");
		else if (st.rtne != null && !Type.VOID.eq(returnType) && !checkTypeEq(rt, returnType))
			error("Return type unmatch");
	}

	private boolean checkTypeEq(Type t1, Type t2) {
		if (t1.eq(t2)) return true;
		if (t1 instanceof POINTER && t2 instanceof POINTER) return true;
		if (Type.CHAR.eq(t1) && (Type.INT.eq(t2) || t2 instanceof POINTER)) return true;
		if (Type.INT.eq(t1) && (Type.CHAR.eq(t2) || t2 instanceof POINTER)) return true;
		if (t1 instanceof POINTER && (Type.INT.eq(t2) || Type.CHAR.eq(t2))) return true;
		return false;
	}
	
	private boolean checkActualInt(Type t) {
		if (Type.CHAR.eq(t) || Type.INT.eq(t) || t instanceof POINTER) return true;
		return false;
	}
	
	private Type checkExpr(Expr e) {
		if (e instanceof AccExpr) return checkAccExpr((AccExpr)e);
		else if (e instanceof AdrsExpr) return checkAdrsExpr((AdrsExpr)e);
		else if (e instanceof CallExpr) return checkCallExpr((CallExpr)e);
		else if (e instanceof CastExpr) return checkCastExpr((CastExpr)e);
		else if (e instanceof CharConst) return checkCharConst((CharConst)e);
		else if (e instanceof Exprs) return checkExprs((Exprs)e);
		else if (e instanceof Num) return checkNum((Num)e);
		else if (e instanceof Op) return checkOp((Op)e);
		else if (e instanceof PostfixExpr) return checkPostfixExpr((PostfixExpr)e);
		else if (e instanceof StringConst) return checkStringConst((StringConst)e);
		else if (e instanceof TypeSizeExpr) return checkTypeSizeExpr((TypeSizeExpr)e);
		else if (e instanceof UnaryExpr) return checkUnaryExpr((UnaryExpr)e);
		else if (e instanceof Var) return checkVar((Var)e);
		else {
			fatalError("translateExpr");
			return null;
		}
	}

	private Type checkAccExpr(AccExpr e) {
		if (!checkLeftOprand(e.expr)) {
			error("accumulator must be used on a leftoprand");
			return Type.VOID;
		}
		Type t = checkExpr(e.expr);
		if (!checkActualInt(t)) {
			error("Type error in accexpr: must be int/char/pointer"); 
			return Type.VOID;
		}
		return e.type = t;
	}

	private Type checkAdrsExpr(AdrsExpr e) {
		Type t1 = checkExpr(e.ob);
		Type t2 = checkExpr(e.sit);
		if (!(t1 instanceof POINTER)) {
			error("Type error in AdrsExpr: base should be a pointer");
			return Type.VOID;
		}
		else if (!(Type.INT.eq(t2) || Type.CHAR.eq(t2))) {
			error("Type error in AdrsExpr: sit should be an integer");
			return Type.VOID;
		}
		return e.type = ((POINTER)t1).elementType;
	}

	private Type checkCallExpr(CallExpr e) {
		Type pe = checkExpr(e.pfe);
		if (!(pe instanceof FUNCTION)) {
			error("Type error in CallExpr: can only call function");
			return Type.VOID;
		} else if (!checkArguments(e.args, (FUNCTION)pe)) {
			error("Type error in CallExpr: ArgumentType mismatch");
			return Type.VOID;
		}
		return e.type = ((FUNCTION)pe).returnType;
	}

	private boolean checkArguments(Arguments args, FUNCTION f) {
		RECORD agms = f.argumentType;
		int s = 0, i = 0;;
		if (agms != null) s = args.list.size();
		while (agms != null) {
			if (i == s) return false;
			Type t = checkExpr(args.list.get(i));
			if (!checkTypeEq(agms.first.type, t)) return false;
			agms = agms.tail; ++i;
		}
		//System.out.println("!!!!" + f.label);
		if (i < s && !f.extend) return false;
		else if (i < s && f.extend) {
			while (i < s) {
				checkExpr(args.list.get(i));
				++i;
			}
		}
		return true;
	}

	private Type checkCastExpr(CastExpr e) {
		Type t = checkTypeName(e.tn);
		checkExpr(e.expr);
		if (t == null) {
			error("Type not found in CastExpr");
			return Type.VOID;
		}
		return e.type = t;
	}

	private Type checkTypeName(TypeName type) {
		int ptrcount = 0;
		while (type instanceof PtrTypeName) {
			type = ((PtrTypeName)type).typename;
			++ptrcount;
		}
		
		Type t = checkTy(((SmpTypeName)type).type);
		if (t == null) return null;
		else if (t instanceof NAME && ptrcount == 0) return null;
		
		return fetchPtrType(t, ptrcount);
	}

	private Type checkCharConst(CharConst e) {
		return e.type = Type.CHAR;
	}

	private Type checkExprs(Exprs e) {
		Type t = null;
		for (Expr i: e.list) 
			t = checkExpr(i);
		return e.type = t;
	}

	private Type checkNum(Num e) {
		return e.type = Type.INT;
	}

	private int checkOpType(Op e) {
		if (e.opType == Op.OpType.ASSIGN) return 1;
		else if (e.opType == Op.OpType.PLUS) return 2;
		else if(e.opType == Op.OpType.LT || e.opType == Op.OpType.GT ||
				e.opType == Op.OpType.EQ || e.opType == Op.OpType.NE || e.opType == Op.OpType.LE ||
				e.opType == Op.OpType.GE || e.opType == Op.OpType.PARAAND || e.opType == Op.OpType.PARAOR)
			return 3;
		else if (e.opType == Op.OpType.MINUS) return 4;
		else if (e.opType == Op.OpType.TIMES || e.opType == Op.OpType.DIVIDE || e.opType == Op.OpType.MOD ||
				e.opType == Op.OpType.AND || e.opType == Op.OpType.OR || e.opType == Op.OpType.XOR ||
				e.opType == Op.OpType.SHL || e.opType == Op.OpType.SHR) return 5;
		else if (e.opType == Op.OpType.ADD_ASSIGN) return 6;
		else if (e.opType == Op.OpType.SUB_ASSIGN) return 7;
		else if (e.opType == Op.OpType.MUL_ASSIGN || e.opType == Op.OpType.DIV_ASSIGN ||
				e.opType == Op.OpType.MOD_ASSIGN || e.opType == Op.OpType.SHL_ASSIGN ||
				e.opType == Op.OpType.SHR_ASSIGN || e.opType == Op.OpType.AND_ASSIGN ||
				e.opType == Op.OpType.XOR_ASSIGN || e.opType == Op.OpType.OR_ASSIGN) return 8;
		return 0;
	}
	
	private Type checkOp(Op e) {
		Op now = (Op)e;
		int mark = checkOpType(now);
		Type t1 = null, t2 = null;
		if (mark == 1) {
			if (!checkLeftOprand(e.left)) {
				error("LeftOprand mismatch");
				return Type.VOID;
			}
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkTypeEq(t1, t2)) {
				error("Type mismatch in assign expr");
				return Type.VOID;
			}
			return e.type = t1;
		} else if (mark == 2) {
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkAddType(t1, t2)) {
				error("Type mismatch in add expr");
				return Type.VOID;
			}
			Type tmp = getAddType(t1, t2);
			return e.type = tmp;
		} else if (mark == 3) {
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!(checkActualInt(t1) && checkActualInt(t2))) {
				error("Type mismatch in logic expr");
				return Type.VOID;
			}
			return e.type = Type.INT;
		} else if (mark == 4) {
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkMinusType(t1, t2)) {
				error("Type mismatch in minus expr");
				return Type.VOID;
			}
			return e.type = Type.INT;
		} else if (mark == 5) {
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkSmpType(t1, t2)) {
				error("Type mismatch in smp expr");
				return Type.VOID;
			}
			return e.type = Type.INT;
		} else if (mark == 6) {
			if (!checkLeftOprand(e.left)) {
				error("LeftOprand mismatch");
				return Type.VOID;
			}
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkAddType(t1, t2)) {
				error("Type mismatch in add_assign expr");
				return Type.VOID;
			}
			Type tmp = getAddType(t1, t2);
			return e.type = tmp;
		} else if (mark == 7) {
			if (!checkLeftOprand(e.left)) {
				error("LeftOprand mismatch");
				return Type.VOID;
			}
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkMinusType(t1, t2)) {
				error("Type mismatch in minus_assign expr");
				return Type.VOID;
			}
			return e.type = Type.INT;
		} else if (mark == 8) {
			if (!checkLeftOprand(e.left)) {
				error("LeftOprand mismatch");
				return Type.VOID;
			}
			t1 = checkExpr(e.left);
			t2 = checkExpr(e.right);
			if (!checkSmpType(t1, t2)) {
				error("Type mismatch in smp_assign expr");
				return Type.VOID;
			}
			return e.type = Type.INT;
		}
		return null;
	}

	private boolean checkSmpType(Type t1, Type t2) {
		if (Type.CHAR.eq(t1) && (Type.CHAR.eq(t2) || Type.INT.eq(t2))) return true;
		else if (Type.INT.eq(t1) && (Type.CHAR.eq(t2) || Type.INT.eq(t2))) return true;
		else return false;
	}

	private boolean checkMinusType(Type t1, Type t2) {
		if (t1 instanceof POINTER) {
			if (t2 instanceof POINTER) {
				if (checkActualSame((POINTER)t1, (POINTER)t2)) return true;
				else return false;
			} else if (Type.INT.eq(t2) || Type.CHAR.eq(t2)) return true;
			else return false;
		} else {
			if (Type.INT.eq(t2) || Type.CHAR.eq(t2)) return true;
			else return false;
		}
	}

	private boolean checkActualSame(Type t1, Type t2) {
		int pc1 = 0, pc2 = 0;
		while (t1 instanceof POINTER) {
			++pc1;
			t1 = ((POINTER)t1).elementType;
		}
		while (t2 instanceof POINTER) {
			++pc2;
			t2 = ((POINTER)t2).elementType;
		}
		if (pc1 == pc2) {
			if (!(t1 instanceof NAME || t2 instanceof NAME)) {
				if (t1.eq(t2)) return true;
				else return false;
			} else {
				Type tmp1 = null, tmp2 = null;
				if (t1 instanceof NAME) tmp1 = checkNameType((NAME)t1); 
				else tmp1 = t1;
				if (t2 instanceof NAME) tmp2 = checkNameType((NAME)t2); 
				else tmp2 = t2;
				if (tmp1 == null || tmp2 == null) {
					error("Type definition imcomplete in minus expr");
					return false;
				} else if (tmp1.eq(tmp2)) return true;
				else return false;
			}
		} else return false;
	}

	private Type getAddType(Type t1, Type t2) {
		if (t1 instanceof POINTER) return t1;
		else if (t2 instanceof POINTER) return t2;
		else return Type.INT;
	}

	private boolean checkAddType(Type t1, Type t2) {
		if (Type.CHAR.eq(t1) && (Type.CHAR.eq(t2) || Type.INT.eq(t2) || t2 instanceof POINTER))
			return true;
		else if (Type.INT.eq(t1) && (Type.CHAR.eq(t2) || Type.INT.eq(t2) || t2 instanceof POINTER))
			return true;
		else if (t1 instanceof POINTER && (Type.CHAR.eq(t2) || Type.INT.eq(t2))) return true;
		else return false;
	}

	private boolean checkLeftOprand(Expr e) {
		if (e instanceof Exprs) {
			if (((Exprs) e).list.size() == 1) e = ((Exprs) e).list.getFirst();
			else return false;
		}
		if (e instanceof Var || e instanceof AdrsExpr || e instanceof PostfixExpr ||
			(e instanceof UnaryExpr && ((UnaryExpr)e).utype == UnaryExpr.UnaryType.TIMES)) {
			Type t = checkExpr(e);
			if (t instanceof ARRAY) return false;
			else return true;
		}
		return false;
	}
	
	private Type checkPostfixExpr(PostfixExpr e) {
		Type ans = null;
		Type t = checkExpr(e.pfe);
		if (e.pt == PostfixExpr.PfType.DOT) {
			if (t instanceof STRUCT || t instanceof UNION) 
				ans = checkField(e.symbol, (RECORD)t);
			else if (t instanceof NAME) {
				Type tmp = checkNameType((NAME)t);
				if (tmp == null) {
					error("Type not found: "+((NAME)t).name);
					return Type.VOID;
				}
				else ans = checkField(e.symbol, (RECORD)tmp);
			} else {
				error("Symbol dot left oprand should be a struct/union");
				return Type.VOID;
			}
		} else if (e.pt == PostfixExpr.PfType.PTR) {
			if (t instanceof POINTER) {
				Type tt = ((POINTER)t).elementType;
				if (tt instanceof UNION || tt instanceof STRUCT) 
					ans = checkField(e.symbol, (RECORD)tt);
				else if (t instanceof NAME) {
					Type tmp = checkNameType((NAME)t);
					if (tmp == null) {
						error("Type not found: "+((NAME)t).name);
						return Type.VOID;
					}
					else ans = checkField(e.symbol, (RECORD)tmp);
				} else {
					error("Symbol -> left oprand should be a struct/union pointer");
					return Type.VOID;
				}
			} else {
				error("Symbol -> left oprand should be a struct/union pointer");
				return Type.VOID;
			}
		}
		
		if (ans == null) {
			error("Field not found in PostfixExpr");
			return Type.VOID;
		}
		return e.type = ans;
	}

	private Type checkField(Symbol symbol, RECORD t) {
		while (t != null) {
			if (t.first.fieldName == symbol) return t.first.type;
			t = t.tail;
		}
		return null;
	}

	private Type checkStringConst(StringConst e) {
		return e.type = Type.CHARPTR;
	}

	private Type checkTypeSizeExpr(TypeSizeExpr e) {
		Type t = checkTypeName(e.ty);
		if (t == null) error("Type not found in TypeSizeExpr");
		return e.type = Type.INT;
	}

	private Type checkUnaryExpr(UnaryExpr e) {
		if (e.utype == UnaryExpr.UnaryType.AND) {
			if (checkLeftOprand(e.expr) || e.expr instanceof StringConst) {
				Type t = checkExpr(e.expr);
				return e.type = new POINTER(t);
			} else {
				error("Operation & must follow with a variable or a string const");
				return Type.VOID;
			}
		} else if (e.utype == UnaryExpr.UnaryType.DEC || e.utype == UnaryExpr.UnaryType.INC) {
			if (!checkLeftOprand(e.expr)) {
				error("accumulator must be used on a leftoprand");
				return Type.VOID;
			}
			Type t = checkExpr(e.expr);
			if (!checkActualInt(t)) {
				error("Type error in accexpr: must be int/char/pointer"); 
				return Type.VOID;
			}
			return e.type = t;
		} else if (e.utype == UnaryExpr.UnaryType.MINUS || e.utype == UnaryExpr.UnaryType.PLUS ||
				e.utype == UnaryExpr.UnaryType.TIDLE) {
			Type t = checkExpr(e.expr);
			if (!(Type.INT.eq(t) || Type.CHAR.eq(t))) {
				error("Unary Operator +/-/~ should be followed by a int/char expression");
				return Type.VOID;
			}
			return e.type = t;
		} else if (e.utype == UnaryExpr.UnaryType.NOT) {
			Type t = checkExpr(e.expr);
			if (!checkActualInt(t)) {
				error("Unary Operator ! should be followed by a int/char/pointer expression");
				return Type.VOID;
			}
			return e.type = t;
		} else if (e.utype == UnaryExpr.UnaryType.SIZEOF) {
			checkExpr(e.expr);
			return e.type = Type.INT;
		} else if (e.utype == UnaryExpr.UnaryType.TIMES) {
			Type t = checkExpr(e.expr);
			if (!(t instanceof POINTER)) {
				error("unary operator * must be used on a pointer expression");
				return Type.VOID;
			}
			return e.type = ((POINTER)t).elementType;
		} else {
			fatalError("transUnaryExpr");
			return Type.VOID;
		}
	}

	private Type checkVar(Var e) {
		TableDealer x = env.vars.get(e.symbol);
		if (x == null) {
			error("Variable not found: " + e.symbol);
			return Type.VOID;
		}
		return e.type = ((VarEntry)x.value).type;
	}
}