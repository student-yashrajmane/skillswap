package com.nt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nt.DTO.EmailDto;
import com.nt.DTO.LoginRequest;
import com.nt.DTO.UserDTO;
import com.nt.adminService.ActivityLoggerService;
import com.nt.adminService.IFrontDataService;
import com.nt.model.Authority;
import com.nt.model.Profile;
import com.nt.model.Users;
import com.nt.oath.GoogleLoginRequest;
import com.nt.repositary.IProfileRepo;
import com.nt.repositary.IUserRepo;
import com.nt.security.JWTUtils;
import com.nt.service.IProfileService;
import com.nt.service.IRegisterUserService;
import com.nt.service.IUserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private IRegisterUserService registerUserService;

    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private IProfileService profileService;

    @Autowired
    private JWTUtils jwtUtils;
    
    @Autowired
	private IUserService userService;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private IUserRepo userRepo;
    
    @Autowired
    private IProfileRepo profileRepo;
    
    @Autowired
	private IFrontDataService frontDataService;
    @Autowired
    private  ActivityLoggerService activityLoggerService;
    
    @GetMapping("/get-aboutPage")
	public ResponseEntity<?> getAboutData()
	{
		Map<String,Object> map = frontDataService.getAboutPage();
		return ResponseEntity.ok(map);
	}
    
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDto) {
    	try {
        Boolean flag = registerUserService.registerUser(userDto);
        System.out.println("SignUp controller HIT");

        if (!flag) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Username " + userDto.getUsername() + " is already taken.");
        }

        // If registration successful, authenticate user automatically
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
        );

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        String accessToken = jwtUtils.generateAccessToken(authentication.getName(), role);
        String refreshToken = jwtUtils.generateRefreshToken(authentication.getName());
      
      
        Map<String, String> response = new HashMap<>();
        response.put("token", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("username", authentication.getName());
    
        response.put("role", role);
        
        

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());    		
    	}
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest logRequest) {
        try {
            // 1. Authenticate
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(logRequest.getUsername(), logRequest.getPassword())
            );
            
            // 2. Set Context immediately
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. IMPROVED ROLE EXTRACTION: Check for Admin Priority
            List<String> authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Logic: If they have ROLE_ADMIN anywhere in their list, use that.
            String role = authorities.contains("ROLE_ADMIN") ? "ROLE_ADMIN" : "ROLE_USER";

            // 4. Generate Tokens and Info
            String accessToken = jwtUtils.generateAccessToken(authentication.getName(), role);
            String refreshToken = jwtUtils.generateRefreshToken(authentication.getName());
            String fullname = profileService.getName(authentication.getName());

            // 5. Account Status Checks
            Optional<Users> userOpt = userService.findUser(authentication.getName());
            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                if (!user.getEnabled()) {
                    return ResponseEntity.status(HttpStatus.LOCKED).body("Account Blocked");
                }
                // If user exists but hasn't set up a profile (except Admins)
                if (user.getProfile() == null && !role.equals("ROLE_ADMIN")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user.getUsername());
                }
            }

            // 6. Final Response
            Map<String, String> response = new HashMap<>();
            response.put("token", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("username", authentication.getName());
            response.put("fullname", fullname);
            response.put("role", role);
            activityLoggerService.logActivity("LOG IN", "@"+authentication.getName()+" logged in!");

            return ResponseEntity.ok(response);
           
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
        }
    }
    
    
    
    @PostMapping("/google")
    public ResponseEntity<?> handleGoogleOAuth(@RequestBody GoogleLoginRequest request) {
        try {
            // 1. Exchange the Access Token for User Info from Google
            String url = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + request.getToken();
            RestTemplate restTemplate = new RestTemplate();
            
            // Google returns a JSON object we map to a Map
            Map<String, Object> payload = restTemplate.getForObject(url, Map.class);

            if (payload != null && payload.containsKey("sub")) {
                String googleId = (String) payload.get("sub"); // The permanent unique ID
                String name = (String) payload.get("name");
                String email = (String) payload.get("email");

                // 2. Check if this Google user already exists in our Profile table
                Profile profile = profileService.findByGoogleId(googleId);

                Map<String, Object> response = new HashMap<>();
                
                if (profile == null) {
                    // --- PATH A: NEW USER (First time logging in with Google) ---
                    response.put("isNewUser", true);
                    response.put("googleId", googleId);
                    response.put("tempName", name);
                    response.put("email", email);
                    
                    
                    
                    // We send a success response but tell React to go to 'Create Profile'
                    activityLoggerService.logActivity("LOG IN", "@"+name+" registered!");
                    return ResponseEntity.ok(response);
                } else {
                    // --- PATH B: EXISTING USER (They have a profile linked to this Google ID) ---
                    // Generate your app's standard JWT token
                	String accessToken = jwtUtils.generateAccessToken(profile.getUser().getUsername(), "ROLE_USER");
                	String refreshToken = jwtUtils.generateRefreshToken(profile.getUser().getUsername());

                    response.put("isNewUser", false);
                    response.put("token", accessToken);
                    response.put("refreshToken", refreshToken);
                    response.put("username", profile.getUser().getUsername());
                    activityLoggerService.logActivity("LOG IN", "@"+profile.getUser().getUsername()+" logged in!");

                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google Token");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Authentication Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/createProfileGoogle")
	public ResponseEntity<?> createProfileDataGoogle(@RequestBody Map<String,Object> data)
	{
    	try {
		String username = (String) data.get("username");
		String googleId = (String) data.get("googleId");
		
		Users user = new Users();
		user.setUsername(username);
		user.setPassword(encoder.encode((String) data.get("password")));
		user.setEnabled(true);
		
		Authority auth = new Authority();
		auth.setAuthority("ROLE_USER");
		auth.setUser(user);
		
		user.setAuthority(Set.of(auth));
		
		Profile profile = new Profile();
		profile.setFullName((String) data.get("fullName"));
		profile.setProfessionalTitle((String) data.get("professionalTitle"));
		profile.setBio((String) data.get("bio"));
		profile.setSkills((String) data.get("skills"));
		profile.setGoogleId(googleId);
		profile.setCoins(5L);
		
		user.setProfile(profile);
		profile.setUser(user);
		
		userRepo.save(user);
		
		String accessToken = jwtUtils.generateAccessToken(profile.getUser().getUsername(), "ROLE_USER");
		String refreshToken = jwtUtils.generateRefreshToken(profile.getUser().getUsername());
		
		   activityLoggerService.logActivity("LOG IN", "@"+username+" registered and created profile!");
		
		return ResponseEntity.ok(Map.of(
			    "exists", true,
			    "token", accessToken,
			    "refreshToken", refreshToken,
			    "username", user.getUsername(),
			    "fullname", profile.getFullName(),
			    "role", "ROLE_USER"
			));
	
	}
    catch(Exception e)
    {
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User need to register");
    }
	}
    
    
    @PostMapping("/google-check")
    public ResponseEntity<?> checkGoogleUser(@RequestBody Map<String, String> request) {
        String googleId = request.get("googleId");
        Optional<Profile> profileOpt = profileRepo.findByGoogleId(googleId);

        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();
            Users user = profile.getUser();
           if(!user.getEnabled()) 
          {
        	   return ResponseEntity.status(HttpStatus.LOCKED).body("Account Blocked");
           	}
            String accessToken = jwtUtils.generateAccessToken(user.getUsername(), "ROLE_USER");
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());
            
            return ResponseEntity.ok(Map.of(
            	    "exists", true,
            	    "token", accessToken,
            	    "refreshToken", refreshToken,
            	    "username", user.getUsername(),
            	    "fullname", profile.getFullName(),
            	    "role", "ROLE_USER"
            	));
        }
        
        return ResponseEntity.ok(Map.of("exists", false));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {

        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || !jwtUtils.isRefreshTokenValid(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or Expired Refresh Token");
            }

            // Extract username from refresh token
            String username = jwtUtils.extractUsername(refreshToken);

            // Get user to extract role
            Optional<Users> userOpt = userService.findUser(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not found");
            }

            String role = userOpt.get().getAuthority().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_USER");

            // Generate new Access Token
            String newAccessToken = jwtUtils.generateAccessToken(username, role);

            Map<String, String> response = new HashMap<>();
            response.put("token", newAccessToken);
            activityLoggerService.logActivity("LOG IN", "@"+username+" logged in!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Token refresh failed");
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Validated @RequestBody EmailDto email)
    {
    	try {
    	userService.sendResetLink(email);
    	return ResponseEntity.ok("Reset Link sent to email");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return ResponseEntity.ok("Please Enter valid credentials");
    	}
   
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String,String> data) {
        try {
            String msg = registerUserService.resetPassword(data.get("username"), data.get("newPassword"));
            return ResponseEntity.ok(msg); // Return the actual success message from service
        }
        catch(Exception e) {
            // Use .badRequest() or .status(500) so the frontend knows it failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    
  

}