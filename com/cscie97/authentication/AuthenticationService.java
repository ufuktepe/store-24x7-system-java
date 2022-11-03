package com.cscie97.authentication;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the API used by clients of the Authentication Service. It contains Users, Entitlements, and Resources.
 *
 * @author Burak Ufuktepe
 */
public class AuthenticationService implements Visitable {

	/**
	 * Unique instance of the AuthenticationService to implement the Singleton Pattern.
	 */
	private static AuthenticationService uniqueInstance;

	/**
	 * Unique ID of the root account which has full AuthenticationService access.
	 */
	private static final String rootAccountId = "root";

	/**
	 * Default password of the root account.
	 */
	private static final String rootAccountPassword = "Default.1234";

	/**
	 * Map of user IDs and user objects.
	 */
	private Map<String, User> userMap;

	/**
	 * Map of resource IDs and resource objects.
	 */
	private Map<String, Resource> resourceMap;

	/**
	 * Map of entitlement IDs and entitlement objects.
	 */
	private Map<String, Entitlement> entitlementMap;

	/**
	 * User that is currently logged in to the AuthenticationService.
	 */
	private User loggedInUser;

	/**
	 * Class constructor that initializes the userMap, resourceMap, entitlementMap and generates the root account.
	 */
	private AuthenticationService() {
		this.userMap = new HashMap<>();
		this.resourceMap = new HashMap<>();
		this.entitlementMap = new HashMap<>();
		this.createRootAccount();
	}

	/**
	 * If the uniqueInstance parameter is null, generate a new AuthenticationService instance and assign it to
	 * uniqueInstance. Return the uniqueInstance. This method supports the Singleton Pattern.
	 *
	 * @return  The uniqueInstance
	 */
	public static AuthenticationService getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new AuthenticationService();
		}
		return uniqueInstance;
	}

	/**
	 * Generates a user for the root account, adds it to userMap, and sets its password to rootAccountPassword.
	 */
	private void createRootAccount() {
		// Create a user for the root account
		User root = new User(rootAccountId, "default root account");
		this.userMap.put(root.getId(), root);

		// Set the password
		try {
			root.setPassword(rootAccountPassword);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Checks if the logged-in user has create_user permission and if the given user ID already exists. Then, creates
	 * a new User for the given ID and name. Adds the User to userMap.
	 *
	 * @param id  Unique ID of the user
	 * @param name  Name of the user
	 * @return  The User object
	 * @throws AuthenticationException  if the given user ID is not unique
	 */
	public User defineUser(String id, String name) throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("create_user");

		// Check if the given user ID already exists.
		if (this.userMap.containsKey(id)) {
			throw new AuthenticationException("define user", id + " already exists");
		}

		User user = new User(id, name);
		this.userMap.put(user.getId(), user);

		System.out.println("User created.");

		return user;
	}

	/**
	 * Checks if the logged-in user has create_entitlement permission. Then creates a new Permission for the given ID,
	 * name, and description. Adds the Permission to entitlementMap.
	 *
	 * @param id  Unique ID of the permission
	 * @param name  Name of the permission
	 * @param description  Description of the permission
	 * @return  The Permission object
	 * @throws AuthenticationException  if the permission already exists
	 */
	public Permission definePermission(String id, String name, String description) throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("create_entitlement");

		// Check if the permission already exists
		if (this.entitlementMap.containsKey(id)) {
			throw new AuthenticationException("define permission", id + " already exists");
		}

		Permission permission = new Permission(id, name, description);
		this.entitlementMap.put(permission.getId(), permission);

		System.out.println(permission.getId() + " created.");

		return permission;
	}

	/**
	 * Checks if the logged-in user has create_entitlement permission. Then creates a new Role for the given ID,
	 * name, and description. Adds the Role to entitlementMap.
	 *
	 * @param id  Unique ID of the role
	 * @param name  Name of the role
	 * @param description  Description of the role
	 * @return  The Role object
	 * @throws AuthenticationException  if the role already exists
	 */
	public Role defineRole(String id, String name, String description) throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("create_entitlement");

		// Check if the role already exists
		if (this.entitlementMap.containsKey(id)) {
			throw new AuthenticationException("define role", id + " already exists");
		}

		Role role = new Role(id, name, description);
		this.entitlementMap.put(role.getId(), role);

		System.out.println(role.getId() + " created.");

		return role;
	}

	/**
	 * Checks if the logged-in user has create_resource permission. Then creates a new Resource for the given ID and
	 * description. Adds the Resource to resourceMap.
	 *
	 * @param id  Unique ID of the resource
	 * @param description  Description of the resource
	 * @return  The Resource object
	 * @throws AuthenticationException  if the given resource ID already exists.
	 */
	public Resource defineResource(String id, String description) throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("create_resource");

		if (this.resourceMap.containsKey(id)) {
			throw new AuthenticationException("define resource", id + " already exists");
		}

		Resource resource = new Resource(id, description);
		this.resourceMap.put(resource.getId(), resource);

		System.out.println(resource.getId() + " created.");

		return resource;
	}

	/**
	 * Performs the following checks:
	 * - if the logged-in user has create_entitlement permission
	 * - if the resource role ID already exists
	 * - if the given role ID corresponds to a role object
	 * - if the given resource ID exists
	 *
	 * Then creates a new ResourceRole for the given ID, name, description, role ID, and resource ID. Adds the
	 * ResourceRole to entitlementMap.
	 *
	 * @param id  Unique ID of the ResourceRole
	 * @param name  Name of the ResourceRole
	 * @param description  Description of the ResourceRole
	 * @param roleId  Unique ID of the Role
	 * @param resourceId  Unique ID of the Resource
	 * @return  The ResourceRole object
	 * @throws AuthenticationException  if any of the above checks fails.
	 */
	public ResourceRole defineResourceRole(String id, String name, String description, String roleId, String resourceId)
			throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("create_entitlement");

		// Check if the resource role ID already exists
		if (this.entitlementMap.containsKey(id)) {
			throw new AuthenticationException("define resource role", id + " already exists");
		}

		// Validate the role ID
		if (!(this.entitlementMap.containsKey(roleId) || this.entitlementMap.get(roleId) instanceof Role)) {
			throw new AuthenticationException("define resource role", roleId + " is not a valid role");
		}

		// Check if the resource ID exists
		if (!this.resourceMap.containsKey(resourceId)) {
			throw new AuthenticationException("define resource role", resourceId + " does not exist");
		}

		Entitlement role = this.entitlementMap.get(roleId);
		Resource resource = this.resourceMap.get(resourceId);

		ResourceRole resourceRole = new ResourceRole(id, name, description, role, resource);
		this.entitlementMap.put(resourceRole.getId(), resourceRole);

		System.out.println(resourceRole.getId() + " created.");

		return resourceRole;
	}

	/**
	 * Checks if the logged-in user has update_entitlement permission.
	 * Performs the following checks:
	 * - if the logged-in user has update_entitlement permission
	 * - if the given entitlement ID exists
	 * - if the given role ID exists
	 * - if the given role ID corresponds to a Role
	 * - if the given role already contains the given entitlement
	 * Then adds the entitlement to the role.
	 *
	 * @param entitlementId  Unique ID of the entitlement
	 * @param roleId  Unique ID of the role
	 * @return  The Role object
	 * @throws AuthenticationException  if any of the checks above fail
	 */
	public Role addEntitlementToRole(String entitlementId, String roleId) throws AuthenticationException {
		// Check if the current user has the required permission
		this.checkPermission("update_entitlement");

		// Check if the given entitlement ID exists
		if (!this.entitlementMap.containsKey(entitlementId)) {
			throw new AuthenticationException("add entitlement to role", entitlementId + " does not exist");
		}

		// Check if the given role ID exists
		if (!this.entitlementMap.containsKey(roleId)) {
			throw new AuthenticationException("add entitlement to role", roleId + " does not exist");
		}

		// Check if the given role ID corresponds to a Role
		if (!(this.entitlementMap.get(roleId) instanceof Role)) {
			throw new AuthenticationException("add entitlement to role", roleId + " is not a role");
		}

		Role role = (Role) this.entitlementMap.get(roleId);
		Entitlement entitlement = this.entitlementMap.get(entitlementId);

		// Check if the given role already contains the given entitlement
		if (role.hasEntitlement(entitlement)) {
			throw new AuthenticationException("add entitlement to role", roleId + " already includes " + entitlementId);
		}

		role.addEntitlement(entitlement);

		System.out.println(entitlement.getId() + " added to " + role.getId());

		return role;
	}

	/**
	 * Checks if the logged-in user has update_user permission. Validates the credential type and user ID. Then, adds
	 * the credential to the given user.
	 *
	 * @param userId  Unique ID of the user
	 * @param type  Credential type (FACE_PRINT, VOICE_PRINT, PASSWORD)
	 * @param value  Credential value (String for the face print, voice print or password)
	 * @throws AuthenticationException  if the given type is not a valid credential type, the given user ID does not
	 * exist or the given value does not meet password requirements (if the type is PASSWORD).
	 */
	public void addUserCredential(String userId, String type, String value) throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("update_user");

		// Validate the credential type
		CredentialType credentialType;
		try {
			credentialType = CredentialType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthenticationException("add user credential", type + " is not a valid credential type");
		}

		// Validate the user ID
		if (!this.userMap.containsKey(userId)) {
			throw new AuthenticationException("add user credential", userId + " does not exist");
		}

		if (credentialType == CredentialType.FACE_PRINT) {
			this.userMap.get(userId).setFacePrint(value);
		} else if (credentialType == CredentialType.VOICE_PRINT) {
			this.userMap.get(userId).setVoicePrint(value);
		} else {
			try {
				this.userMap.get(userId).setPassword(value);
			} catch (IllegalArgumentException e) {
				throw new AuthenticationException("add user credential", e.getMessage());
			}
		}
	}

	/**
	 * Checks if the logged-in user has update_user permission, the given user ID and entitlement ID exist. Then
	 * calls the addEntitlement method on the user.
	 *
	 * @param userId  Unique ID of the user
	 * @param entitlementId  Unique ID of the entitlement
	 * @throws AuthenticationException  if the given user ID or entitlement ID doesn't exist.
	 */
	public void addUserEntitlement(String userId, String entitlementId) throws AuthenticationException {

		// Check if the current user has the required permission
		this.checkPermission("update_user");

		// Check if the given user ID exists
		if (!this.userMap.containsKey(userId)) {
			throw new AuthenticationException("add user entitlement", userId + " does not exist");
		}

		// Check if the given entitlement ID exists
		if (!this.entitlementMap.containsKey(entitlementId)) {
			throw new AuthenticationException("add user entitlement", entitlementId + " does not exist");
		}

		try {
			this.userMap.get(userId).addEntitlement(this.entitlementMap.get(entitlementId));
		} catch (IllegalArgumentException e) {
			throw new AuthenticationException("add user entitlement", e.getMessage());
		}

		System.out.println(entitlementId + " added to " + userId);
	}

	/**
	 * Iterate over the userMap to find a match for the given username/password combination. If a match is found,
	 * logout the current user and login the found user.
	 *
	 * @param username  User's username
	 * @param password  User's password
	 * @return  AuthToken of the user
	 * @throws AuthenticationException  if no match is found for the given username/password combination
	 */
	public AuthToken login(String username, String password) throws AuthenticationException {
		for (Map.Entry<String, User> entry : this.userMap.entrySet()) {
			User user = entry.getValue();
			if (user.login(username.toLowerCase(), password)) {

				// Logout the current user
				if (this.loggedInUser != null) {
					if (this.loggedInUser != user) {
						this.logout();
					}
				}

				// Login the given user
				if (this.loggedInUser == null) {
					this.loggedInUser = user;
					System.out.println(this.loggedInUser.getId() + " logged in.");
				}

				return this.loggedInUser.getValidAuthToken();
			}
		}
		throw new AuthenticationException("login", "invalid username/password");
	}

	/**
	 * Checks if a user is logged-in to the system. Then, logs out the current user.
	 *
	 * @throws AuthenticationException  if no user is logged in.
	 */
	public void logout() throws AuthenticationException {
		// Check if a user is logged in
		if (this.loggedInUser == null) {
			throw new AuthenticationException("logout", "currently no user is logged in");
		}

		try {
			this.loggedInUser.logout();
		} catch (IllegalArgumentException e) {
			throw new AuthenticationException("logout", e.getMessage());
		}

		System.out.println(this.loggedInUser.getId() + " logged out.");
		this.loggedInUser = null;
	}

	/**
	 * Checks if the logged-in user has get_user_authtoken permission. Retrieves the current AuthToken of the
	 * logged-in user.
	 *
	 * @return  AuthToken of the current user
	 * @throws AuthenticationException  if the current user does not have get_user_authtoken permission or if no user
	 * is logged-in currently.
	 */
	public AuthToken getAuthTokenOfCurrentUser() throws AuthenticationException {
		// Check if the current user has the required permission
		this.checkPermission("get_user_authtoken");

		return this.loggedInUser.getExistingAuthToken();
	}

	/**
	 * Checks if the logged-in user has get_user_authtoken permission. Iterates over each user and looks for a
	 * matching biometricId. Once a match is found, returns a valid (unexpired) AuthToken for the user.
	 *
	 * @param biometricId  face print or voice print of the user
	 * @return  A valid AuthToken of the user
	 * @throws AuthenticationException  if no matching biometric ID is found.
	 */
	public AuthToken getAuthToken(String biometricId) throws AuthenticationException {
		// Check if the current user has the required permission
		this.checkPermission("get_user_authtoken");

		// Iterate over each user and look for a matching biometricId
		for (Map.Entry<String, User> entry : this.userMap.entrySet()) {
			User user = entry.getValue();
			if (user.validateBiometricId(biometricId)) {
				return user.getValidAuthToken();
			}
		}
		throw new AuthenticationException("get auth token", "no matching biometric ID found");
	}

	/**
	 * Checks if the logged-in user has get_user_authtoken permission. Iterates over each user and looks for a
	 * matching username/password combination. Once a match is found, returns a valid (unexpired) AuthToken for the
	 * user.
	 *
	 * @param username  User's username
	 * @param password  User's password
	 * @return  A valid AuthToken of the user
	 * @throws AuthenticationException  if no match is found for the given username/password combination.
	 */
	public AuthToken getAuthToken(String username, String password) throws AuthenticationException {
		// Check if the current user has the required permission
		this.checkPermission("get_user_authtoken");

		// Iterate over each user and look for a matching username/password combination
		for (Map.Entry<String, User> entry : this.userMap.entrySet()) {
			User user = entry.getValue();
			if (user.login(username.toLowerCase(), password)) {
				return user.getValidAuthToken();
			}
		}
		throw new AuthenticationException("get auth token", "invalid username/password");
	}

	/**
	 * Creates an AuthenticationVisitor instance and calls its visitor method. Checks if the user associated with the
	 * Auth Token has the required permission for the given permission ID and resource ID. This method supports the
	 * Visitor Pattern.
	 *
	 * @param permissionId  Unique ID of the permission
	 * @param resourceId  Unique ID of the resource (may be null)
	 * @param authToken  Authorization Token
	 * @throws InvalidAuthTokenException  if the given authToken is invalid
	 * @throws AccessDeniedException  if the authToken does not have the required permission
	 */
	public void checkPermission(String permissionId, String resourceId, String authToken)
			throws InvalidAuthTokenException, AccessDeniedException {
		// Create an AuthenticationVisitor instance
		AuthenticationVisitor visitor = new AuthenticationVisitor(resourceId, permissionId, authToken);

		// Call the accept method of the AuthenticationService to accept the visitor
		this.accept(visitor);

		if (visitor.getUserId() == null || !visitor.isTokenActive()) {
			throw new InvalidAuthTokenException("check permission", "invalid auth token");
		}

		if (!visitor.hasPermission()) {
			throw new AccessDeniedException("check permission", visitor.getUserId(), permissionId, resourceId);
		}
	}

	/**
	 * Checks if the logged-in user has the given permission. Utilizes the overloaded checkPermission method.
	 *
	 * @param permissionId  Unique ID of the permission
	 * @throws AuthenticationException  if no user is currently logged-in to the system or if the overloaded
	 * checkPermission throws an InvalidAuthTokenException or AccessDeniedException.
	 */
	public void checkPermission(String permissionId) throws AuthenticationException {
		// Check if a user is logged in
		if (this.loggedInUser == null) {
			throw new AuthenticationException("check permission", "please login to the Authentication Service");
		}

		// Check if the default root account is logged in
		if (this.loggedInUser.getId().equals(rootAccountId)) {
			return;
		}

		try {
			this.checkPermission(permissionId, null, this.loggedInUser.getExistingAuthToken().getId());
		} catch (InvalidAuthTokenException e) {
			throw new AuthenticationException(e.getAction(), e.getReason());
		} catch (AccessDeniedException e) {
			throw new AuthenticationException(e.getAction(), e.getReason());
		}
	}

	/**
	 * Checks if the logged-in user has auth_readonly_role permission. Then, creates an InventoryVisitor instance and
	 * utilizes the Visitor pattern to print out an inventory of all Users, Resources, Auth Tokens, Roles, and
	 * Permissions of the AuthenticationService.
	 *
	 * @throws AuthenticationException  if an error condition occurs in InventoryVisitor class
	 */
	public void getInventory() throws AuthenticationException {
		// Check if the current user has the required permission
		this.checkPermission("auth_readonly_role");

		InventoryVisitor visitor = new InventoryVisitor();
		visitor.visit(this);
		System.out.println(visitor.getInventory());
	}

	/**
	 * Accepts a visitor and calls the visit method of the Visitor per the Visitor Pattern.
	 *
	 * @param visitor  Visitor instance
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Retrieves the map of users.
	 *
 	 * @return  Map of users
	 */
	public Map<String, User> getUserMap() {
		return this.userMap;
	}

	/**
	 * Retrieves the map of resources.
	 *
	 * @return  Map of resources
	 */
	public Map<String, Resource> getResourceMap() {
		return this.resourceMap;
	}

	/**
	 * Retrieves the map of entitlements
	 *
	 * @return  Map of entitlements
	 */
	public Map<String, Entitlement> getEntitlementMap() {
		return this.entitlementMap;
	}


}
