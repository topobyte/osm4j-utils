// Copyright 2015 Sebastian Kuerten
//
// This file is part of osm4j.
//
// osm4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// osm4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with osm4j. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.osm4j.utils.executables;

import java.io.IOException;

import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.utils.AbstractTaskSingleInputIteratorSingleOutput;

public class OsmCat extends AbstractTaskSingleInputIteratorSingleOutput
{

	@Override
	protected String getHelpMessage()
	{
		return OsmCat.class.getSimpleName() + " [options]";
	}

	public static void main(String[] args) throws IOException
	{
		OsmCat convert = new OsmCat();

		convert.setup(args);

		convert.readMetadata = true;
		convert.writeMetadata = true;

		convert.init();

		convert.run();

		convert.finish();
	}

	@Override
	protected void setup(String[] args)
	{
		super.setup(args);
	}

	private void run() throws IOException
	{
		if (inputIterator.hasBounds()) {
			osmOutputStream.write(inputIterator.getBounds());
		}

		while (inputIterator.hasNext()) {
			EntityContainer entityContainer = inputIterator.next();
			switch (entityContainer.getType()) {
			case Node:
				osmOutputStream.write((OsmNode) entityContainer.getEntity());
				break;
			case Way:
				osmOutputStream.write((OsmWay) entityContainer.getEntity());
				break;
			case Relation:
				osmOutputStream
						.write((OsmRelation) entityContainer.getEntity());
				break;
			}
		}
		osmOutputStream.complete();
	}

}