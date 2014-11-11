package compiler2013.util;

import compiler2013.addr.Label;

public class IterLabelDealer {
	public Label begin;
	public Label next;
	
	public IterLabelDealer(Label b, Label n) {
		begin = b;
		next = n;
	}
}
