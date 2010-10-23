/*
	Copyright 2009 Snild Dolkow
	
	This file is part of Perler.
	
	Perler is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Perler is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Perler.  If not, see <http://www.gnu.org/licenses/>.
*/
package se.dolkow.imagefiltering;

import se.dolkow.imagefiltering.internationalization.Messages;

/**
 * @author snild
 *
 */
public class AllocationException extends ImageException {

	public AllocationException(String msg, OutOfMemoryError e) {
		super(Messages.get("AllocationException.out_of_memory") + msg, e); //$NON-NLS-1$
	}

	public AllocationException(OutOfMemoryError e) {
		this(e.getLocalizedMessage(), e); 
	}

	private static final long serialVersionUID = 1L;

}
