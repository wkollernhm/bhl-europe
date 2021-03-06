package com.bhle.access.storage.akubra;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import com.google.common.collect.AbstractIterator;

class WildcardFSBlobIdIterator extends AbstractIterator<URI> {
	private final File baseDir;
	private final String filterPrefix;
	private DirectoryNode currentDir;

	WildcardFSBlobIdIterator(File baseDir, String filterPrefix) {
		if (filterPrefix != null) {
			this.baseDir = new File(baseDir, filterPrefix);
		} else {
			this.baseDir = baseDir;
		}
		this.filterPrefix = filterPrefix;
		currentDir = new DirectoryNode(null, "");
	}

	@Override
	protected URI computeNext() {
		while (currentDir != null) {
			DirectoryNode child = currentDir.nextChild();
			if (child == null) {
				// no more children; move up
				currentDir = currentDir.getParent();
			} else if (child.isDirectory()) {
				// child is dir; move down
				currentDir = child;
			} else {
				// child is file
				URI uri = child.getURI();
				return uri;
			}
		}

		return endOfData(); // exhausted
	}

	private class DirectoryNode {

		private final DirectoryNode parent;

		private final String path;

		private String[] childPaths;

		private int childNum;

		DirectoryNode(DirectoryNode parent, // null if root
				String path) { // "" if root, "name/" if subdir, "name" if file
			this.parent = parent;
			this.path = path;
			if (isDirectory()) {
				setChildPaths();
			}
		}

		private void setChildPaths() {
			File dir;
			if (path.length() == 0) {
				dir = baseDir;
			} else {
				dir = new File(baseDir, path);
			}
			File[] childFiles = dir.listFiles();
			childPaths = new String[childFiles.length];
			for (int i = 0; i < childFiles.length; i++) {
				StringBuilder childPath = new StringBuilder(path);
				try {
					childPath.append(URLEncoder.encode(childFiles[i].getName(),
							"UTF-8"));
				} catch (UnsupportedEncodingException wontHappen) {
					throw new RuntimeException(wontHappen);
				}
				if (childFiles[i].isDirectory()) {
					childPath.append("/");
				}
				childPaths[i] = childPath.toString();
			}
		}

		DirectoryNode getParent() {
			return parent;
		}

		URI getURI() {
			return URI.create(WildcardFSBlob.scheme + ":" + path);
		}

		boolean isDirectory() {
			return path.length() == 0 || path.endsWith("/");
		}

		DirectoryNode nextChild() {
			if (isDirectory()) {
				if (childNum == childPaths.length) {
					return null; // no more children
				} else {
					return new DirectoryNode(this, childPaths[childNum++]);
				}
			} else {
				return null; // not a directory
			}
		}
	}
}
