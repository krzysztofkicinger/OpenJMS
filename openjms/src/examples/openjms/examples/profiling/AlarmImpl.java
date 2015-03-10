/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: AlarmImpl.java,v 1.1 2001/04/17 13:23:27 jima Exp $
 *
 * Date         Author  Changes
 * 01/01/2001   fabien  Created
 */
package openjms.examples.profiling;

import java.io.*;
import java.util.*;

public class AlarmImpl
	implements Serializable
{
	public AlarmImpl( int id,
				      String name,
				      String src,
				      String ct,
				      String type,
				      String sev,
				      long creTime,
				      boolean isSA,
				      boolean isCAuto,
				      String add,
				      List ad,
				      boolean ack,
					  boolean isC )
	{
		this.creTime = creTime;
		this.id = id;
		this.name = name;
		this.src = src;
		this.type = type;
		this.sev = sev;
		this.add = add;
		this.isSA = isSA;
		this.ct = ct;
		this.isC = isC;
		this.isCAuto = isCAuto;
		this.ack = ack;
		this.ad = ad;
	}

	/**
	 * Returns the name of this alarm
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Return the source of this Alarm
	 */
	public String getSource()
	{
		return src;
	}

	/**
	 * Return the creation time of this Alarm
	 */
	public long getCreationTime()
	{
		return creTime;
	}

	/**
	 *
	 */
	public List getAdditionalData()
	{
		return ad;
	}
	
	/**
	 * Return the type/category of this alarm
	 */
	public String getAlarmType()
	{
		return type;
	}

	/**
	 * Return the severity of this alarm
	 */
	public String getAlarmSeverity()
	{
		return sev;
	}

	/**
	 * Return additional text for this alarm
	 */
	public String getAdditionalText()
	{
		return add;
	}

	/**
	 * Does this alarm affect the service ?
	 */
	public boolean isServiceAffecting()
	{
		return isSA;
	}

	/**
	 * Return the condition type of this alarm
	 */
	public String getConditionType()
	{
		return ct;
	}

	/**
	 * Has this alarm been marked as cleared ?
	 *
	 * @return true if setCleared(true) is called. 
	 */
	public boolean isCleared()
	{
		return isC;
	}

	/**
	 * Make this alarm as cleared by an entity other than the NE.
	 *
	 * A GUI widget or alarm correlation routines
	 * can use this method to clear this alarm.
	 */
	//public void setCleared(boolean cleared); // in service

	/**
	 * @deprecated use {@link isAutoCleared} instead
	 */
	public boolean isClearedByNE()
	{
		return isAutoCleared();
	}

	/**
	 * 
	 */
	public boolean isAutoCleared()
	{
		return isCAuto;
	}

	/**
	 * Has this alarm been marked as acknowledged ?
	 *
	 * @return the acknowledgement object associated with this alarm.
	 * Return nulls if this alarm was not acknowledged
	 */
	public boolean getAcknowledged()
	{
		return ack;
	}

	/**
	 *
	 */
	void setCleared( boolean x)
	{
		isC = x;
	}
	
	/**
	 *
	 */
	void setClearedByNE( boolean x)
	{
		isCAuto = x;
	}

	/**
	 *
	 */
	void setAcknowledged( boolean x)
	{
		ack = x;
	}

	/**
	 *
	 */
	public int getAlarmId()
	{
		return id;
	}

	/**
	 *
	 */
	public String toString()
	{
		return getName() + "/"
			+ type + "/"
			+ sev;
	}

	/**
	 *
	 */
	public static void dbg( String s)
	{
		if ( debug)
			System.out.println( "[AlarmImpl] "+ s);
	}

	/**
	 *
	 */
	public static boolean debug = false;
	private long creTime;
	private int id;
	private String name;
	private String src;
	private final String type;
	private final String sev;	
	private final String add;
	private final boolean isSA;
	private final String ct;
	private boolean isC;
	private boolean isCAuto;
	private boolean ack;
	private final List ad;
}
