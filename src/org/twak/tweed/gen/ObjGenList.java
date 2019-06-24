package org.twak.tweed.gen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.twak.siteplan.jme.Jme3z;
import org.twak.tweed.gen.skel.ObjSkelGenList;
import org.twak.tweed.IDumpObjs;
import org.twak.tweed.Tweed;
import org.twak.tweed.handles.HandleMe;
import org.twak.utils.geom.ObjDump;
import org.twak.utils.geom.ObjRead;
import org.twak.utils.ui.ListDownLayout;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ObjGenList extends ObjGen {

	protected String root;
	public File indexFile;

	boolean renderLines = false;
	float transparency = 1;

	transient Geometry geometry;
	
	boolean hasTextures = false;

	public List<String> fileNameList = new ArrayList<String>();
	public List<ObjGen> objGenList = new ArrayList<ObjGen>();

	public ObjGenList() {}

	public ObjGenList(File indexFile, File folder, Tweed tweed) {
		super("list " + folder.getName(), tweed);
		this.root = folder.getName();
		this.indexFile = indexFile;

		try {
			List<String> fileList = Files.lines( indexFile.toPath() ).collect( Collectors.toList() );

			for (String file : fileList) {
				// if there's a new line in our todo.list, this forms a file with String ""
				if (file.length() == 0) continue;

				String filename = root + File.separator + file;
				ObjGen obj = new ObjGen(filename, tweed);
				objGenList.add(obj);
				fileNameList.add(filename);

				System.out.println("adding obj file: " + filename);
			}

		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void calculate() {
		for (ObjGen o : objGenList) {
			o.calculate();
		}
	}

	protected void setTexture(Geometry g, Material mat) {}

	@Override
	public JComponent getUI() {

		JPanel out = new JPanel(new ListDownLayout());

		JButton toSkel = new JButton("convert to skeleton");
		toSkel.addActionListener( e -> toSkeleton() );

		out.add( toSkel );

		return out;
	}

	private void toSkeleton() {
		tweed.frame.addGen( new ObjSkelGenList( tweed, "objskel list: "+name, root , indexFile), true );
		setVisible( false );
	}

	@Override
	public void dumpObj( ObjDump dump ) {
		dump.setCurrentMaterial( Color.pink, 0.5);
		for (String filename : fileNameList) {
			System.out.println("adding " + getRootName() + File.separator + filename);
			dump.addAll (new ObjRead( new File(getRootName() + File.separator + filename)));
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		for (ObjGen o : objGenList) {
			o.setVisible(visible);
		}
	}

	public String getRootName() {
		return Tweed.JME + root ;
	}

}
