package com.ms.codecoverageplugin.svn;

import java.io.Serializable;

public class LineModificationData implements Serializable
{

	private String line;
	public String getLine() {
		return line;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public long getLineNum() {
		return lineNum;
	}

	public String getRevisionNum() {
		return revisionNum;
	}

	private String modifiedBy;
	private long lineNum;
	private String revisionNum;

	public String getStoryId()
	{
		return storyId;
	}

	private String storyId;
	/**
	 * @param line
	 * @param modifiedBy
	 * @param lineNum
	// * @param string
	 */
	public LineModificationData(String line, String modifiedBy, long lineNum,String revisionNum, String storyId) {
		super();
		this.line = line;
		this.modifiedBy = modifiedBy;
		this.lineNum = lineNum;
		this.revisionNum=revisionNum;
		this.storyId=storyId;
	}
	
	@Override
	public String toString() {
		
		return lineNum+" "+revisionNum+" "+storyId+" "+modifiedBy+" "+line;
	}
}
