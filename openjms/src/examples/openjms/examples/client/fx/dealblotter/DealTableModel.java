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
 * $Id: DealTableModel.java,v 1.2 2000/05/14 23:20:15 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */


package openjms.examples.client.fx.dealblotter;

import javax.swing.table.DefaultTableModel; 

/**
 * The deal table model uses the defaultTableModel for most of the work.
 * Class types for the given columns are returned here. Editing of the
 * celss is switch off here.
 *
 * @version     $Revision: 1.2 $ $Date: 2000/05/14 23:20:15 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         openjms.examples.client.fx.dealblotter.DealTable
 *
 **/


public class DealTableModel extends DefaultTableModel
{
	// The underlying data structure holding the array.
	private Object[][]		data_;
	
	// The column title headers.
	private String[]		header_;

	// An array identifying the object type for each column.
	private Class[]			types_;

	/**
	 * Initialise the defaultTableModel and keep referenses to the objects
	 * for conveniance.
	 *
	 * @param data The array of object data to initialise the columns with.
	 * @param header The Column headings.
	 *
	 */
	public DealTableModel(Object[][] data, String[] header, Class[] types)
	{
		super(data, header);
		data_ = data;
		header_ = header;
		types_ = types;
	}

	
	/**
	 * Return the object type for the nominated column.
	 *
	 * @param columnIndex The index of the column.
	 * @return Class The class type for the given column.
	 *
	 */
	public Class getColumnClass (int columnIndex) 
	{
		return types_[columnIndex];
	}

	/**
	 * No editing of the cells is allowed.
	 *
	 * @param row The row index.
	 * @param col The column index.
	 * @return boolean Always false for this table.
	 *
	 */
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	/**
	 * Remove all deals from the deal table.
	 *
	 */
	void removeDeals()
	{
		super.dataVector.clear();
	}
	
	
} // End DealTableModel
