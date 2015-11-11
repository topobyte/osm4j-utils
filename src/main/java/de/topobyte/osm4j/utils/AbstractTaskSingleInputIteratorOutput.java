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

package de.topobyte.osm4j.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.pbf.seq.PbfIterator;
import de.topobyte.osm4j.tbo.access.TboIterator;
import de.topobyte.osm4j.utils.config.PbfConfig;
import de.topobyte.osm4j.utils.config.PbfOptions;
import de.topobyte.osm4j.utils.config.TboConfig;
import de.topobyte.osm4j.utils.config.TboOptions;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public abstract class AbstractTaskSingleInputIteratorOutput extends
		AbstractTask
{

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_INPUT_FORMAT = "input_format";
	private static final String OPTION_OUTPUT_FORMAT = "output_format";

	protected FileFormat inputFormat;
	protected FileFormat outputFormat;
	protected PbfConfig pbfConfig;
	protected TboConfig tboConfig;
	protected String pathInput = null;

	protected boolean readMetadata = true;
	protected boolean writeMetadata = true;

	protected boolean closeInput = true;
	protected InputStream in;

	protected OsmIterator inputIterator;

	public AbstractTaskSingleInputIteratorOutput()
	{
		// @formatter:off
		OptionHelper.add(options, OPTION_INPUT, true, false, "the input file");
		OptionHelper.add(options, OPTION_INPUT_FORMAT, true, true, "the file format of the input");
		OptionHelper.add(options, OPTION_OUTPUT_FORMAT, true, true, "the file format of the output");
		PbfOptions.add(options);
		TboOptions.add(options);
		// @formatter:on
	}

	@Override
	protected void setup(String[] args)
	{
		super.setup(args);

		String inputFormatName = line.getOptionValue(OPTION_INPUT_FORMAT);
		inputFormat = FileFormat.parseFileFormat(inputFormatName);
		if (inputFormat == null) {
			System.out.println("invalid input format");
			System.out.println("please specify one of: "
					+ FileFormat.getHumanReadableListOfSupportedFormats());
			System.exit(1);
		}

		String outputFormatName = line.getOptionValue(OPTION_OUTPUT_FORMAT);
		outputFormat = FileFormat.parseFileFormat(outputFormatName);
		if (outputFormat == null) {
			System.out.println("invalid output format");
			System.out.println("please specify one of: "
					+ FileFormat.getHumanReadableListOfSupportedFormats());
			System.exit(1);
		}

		pbfConfig = PbfOptions.parse(line);
		tboConfig = TboOptions.parse(line);

		pathInput = line.getOptionValue(OPTION_INPUT);
	}

	protected void init() throws IOException
	{
		in = null;
		if (pathInput == null) {
			closeInput = false;
			in = new BufferedInputStream(System.in);
		} else {
			closeInput = true;
			File file = new File(pathInput);
			FileInputStream fis = new FileInputStream(file);
			in = new BufferedInputStream(fis);
		}

		switch (inputFormat) {
		case XML:
			inputIterator = new OsmXmlIterator(in, readMetadata);
			break;
		case TBO:
			inputIterator = new TboIterator(in, readMetadata);
			break;
		case PBF:
			inputIterator = new PbfIterator(in, readMetadata);
			break;
		}
	}

	protected void finish() throws IOException
	{
		if (closeInput) {
			in.close();
		}
	}

}
