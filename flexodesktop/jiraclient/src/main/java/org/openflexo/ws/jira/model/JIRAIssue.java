package org.openflexo.ws.jira.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JIRAIssue extends JIRAObject<JIRAIssue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5078675682772647794L;

	public static class IssueType extends JIRAObject<IssueType> {

		public static final String STACKTRACE_FIELD = "customfield_10000";
		public static final String SYSTEM_PROPERTIES_FIELD = "customfield_10001";

		/**
		 * 
		 */
		private static final long serialVersionUID = -4127968691089024145L;

		private String name;

		private Boolean subtask;

		private String iconUrl;

		private String description;

		private Map<String, JIRAField> fields;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean isSubtask() {
			return subtask;
		}

		public void setSubtask(Boolean subtask) {
			this.subtask = subtask;
		}

		public String getIconUrl() {
			return iconUrl;
		}

		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Map<String, JIRAField> getFields() {
			return fields;
		}

		public void setFields(Map<String, JIRAField> fields) {
			this.fields = fields;
		}

		public JIRAField<JIRAPriority> getPriorityField() {
			if (getFields() != null) {
				try {
					JIRAField<?> jiraField = getFields().get("priority");
					if (jiraField != null) {
						return jiraField.mutateToFieldOfType(JIRAPriority.class);
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		public JIRAField<JIRAVersion> getVersionField() {
			if (getFields() != null) {
				try {
					JIRAField<?> jiraField = getFields().get("versions");
					if (jiraField != null) {
						return jiraField.mutateToFieldOfType(JIRAVersion.class);
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		public JIRAField<JIRAComponent> getComponentField() {
			if (getFields() != null) {
				try {
					JIRAField<?> jiraField = getFields().get("components");
					if (jiraField != null) {
						return jiraField.mutateToFieldOfType(JIRAComponent.class);
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		public boolean hasStacktraceField() {
			return getFields() != null && getFields().get(STACKTRACE_FIELD) != null;
		}

		public boolean hasSystemPropertiesField() {
			return getFields() != null && getFields().get(SYSTEM_PROPERTIES_FIELD) != null;
		}
	}

	private JIRAProject project;
	private String summary;
	private String description;
	private IssueType issuetype;
	private List<JIRAVersion> versions;
	private JIRAPriority priority;
	private List<JIRAComponent> components;
	private transient boolean membersHaveBeenReplaced;

	public JIRAProject getProject() {
		return project;
	}

	public void setProject(JIRAProject project) {
		this.project = project;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public IssueType getIssuetype() {
		return issuetype;
	}

	public void setIssuetype(IssueType issuetype) {
		this.issuetype = issuetype;
	}

	public List<JIRAVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<JIRAVersion> versions) {
		this.versions = versions;
	}

	public JIRAVersion getVersion() {
		if (versions != null && versions.size() > 0) {
			return versions.get(0);
		} else {
			return null;
		}
	}

	public void setVersion(JIRAVersion version) {
		if (version != null) {
			setVersions(Arrays.asList(version));
		} else {
			setVersions(null);
		}
	}

	public List<JIRAComponent> getComponents() {
		return components;
	}

	public void setComponents(List<JIRAComponent> components) {
		this.components = components;
	}

	public JIRAComponent getComponent() {
		if (components != null && components.size() > 0) {
			return components.get(0);
		} else {
			return null;
		}
	}

	public void setComponent(JIRAComponent component) {
		if (component != null) {
			setComponents(Arrays.asList(component));
		} else {
			setComponents(null);
		}
	}

	public JIRAPriority getPriority() {
		return priority;
	}

	public void setPriority(JIRAPriority priority) {
		this.priority = priority;
	}

	public <J extends JIRAObject<J>> void replaceMembersByIdentityMembers() {
		Class<?> klass = getClass();
		while (klass != null) {
			for (Field field : klass.getDeclaredFields()) {
				if (JIRAObject.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						J object = (J) field.get(this);
						if (object != null) {
							field.set(this, object.getAsIdentityObject());
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (List.class.isAssignableFrom(field.getType())
						&& field.getGenericType() instanceof ParameterizedType
						&& ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0] instanceof Class
						&& JIRAObject.class.isAssignableFrom((Class<?>) ((ParameterizedType) field.getGenericType())
								.getActualTypeArguments()[0])) {
					field.setAccessible(true);
					try {
						List<J> list = (List<J>) field.get(this);
						if (list != null) {
							List<J> newList = new ArrayList<J>();
							for (J j : list) {
								newList.add(j.getAsIdentityObject());
							}
							field.set(this, newList);
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			klass = klass.getSuperclass();
		}
		membersHaveBeenReplaced = true;
	}

	public <J extends JIRAObject<J>> void replaceMembersByOriginalMembers() {
		if (!membersHaveBeenReplaced) {
			return;
		}

		Class<?> klass = getClass();
		while (klass != null) {
			for (Field field : klass.getDeclaredFields()) {
				if (JIRAObject.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					try {
						J object = (J) field.get(this);
						if (object != null) {
							field.set(this, object.restoreObject());
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (List.class.isAssignableFrom(field.getType())
						&& field.getGenericType() instanceof ParameterizedType
						&& JIRAObject.class.isAssignableFrom(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]
								.getClass())) {
					field.setAccessible(true);
					try {
						List<J> list = (List<J>) field.get(this);
						if (list != null) {
							List<J> newList = new ArrayList<J>();
							for (J j : list) {
								newList.add(j.restoreObject());
							}
							field.set(this, newList);
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			klass = klass.getSuperclass();
		}
		membersHaveBeenReplaced = false;
	}

	public void makeValid() {
		if (getIssuetype() != null) {
			Class<?> klass = getClass();
			while (klass != HashMap.class) {
				for (Field field : klass.getDeclaredFields()) {
					if (!getIssuetype().getFields().containsKey(field.getName())) {
						if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())
								|| Modifier.isTransient(field.getModifiers())) {
							continue;
						}
						field.setAccessible(true);
						try {
							field.set(this, null);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				klass = klass.getSuperclass();
			}
			for (Map.Entry<String, Object> e : new HashMap<String, Object>(this).entrySet()) {
				if (!getIssuetype().getFields().containsKey(e.getKey())) {
					this.remove(e.getKey());
				}
			}
		}
	}

	public String getStacktrace() {
		return (String) get(IssueType.STACKTRACE_FIELD);
	}

	public void setStacktrace(String stacktrace) {
		put(IssueType.STACKTRACE_FIELD, stacktrace);
	}

	public String getSystemProperties() {
		return (String) get(IssueType.SYSTEM_PROPERTIES_FIELD);
	}

	public void setSystemProperties(String systemProperties) {
		put(IssueType.SYSTEM_PROPERTIES_FIELD, systemProperties);
	}

}
