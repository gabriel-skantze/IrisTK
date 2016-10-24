package iristk.speech;

import iristk.util.Record;

public class Phone extends Record {

	@RecordField
	public String name;
	
	@RecordField
	public Float start;
	
	@RecordField
	public Float end;

	@RecordField
	public boolean prominent = false;

	@RecordField
	public String word;
	
}
