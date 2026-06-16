package com.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.dtos.AccountDTO;
import com.demo.entities.Account;
import com.demo.repositories.AccountRepository;

import jakarta.transaction.Transactional;
@Service("accountService")
public class AccountServiceImpl implements AccountService{
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ModelMapper mapper;
	private BCrypt crypt;


	@Override
	public List<AccountDTO> findAll() {
		return mapper.map(accountRepository.findAll(), new TypeToken<List<AccountDTO>>() {}.getType());
	}

	@Override
	public boolean save(AccountDTO accountDTO) {
		try {
			if (accountDTO == null || accountDTO.getUsername() == null || accountDTO.getUsername().trim().isEmpty()) {
				return false;
			}
			String normalizedUsername = accountDTO.getUsername().trim();
			boolean duplicateUsername = accountDTO.getId() == null
					? accountRepository.existsByUsernameIgnoreCase(normalizedUsername)
					: accountRepository.existsByUsernameIgnoreCaseAndIdNot(normalizedUsername, accountDTO.getId());
			if (duplicateUsername) {
				return false;
			}
			accountDTO.setUsername(normalizedUsername);
			Account account =  mapper.map(accountDTO, Account.class);
			accountRepository.save(account);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean login1(String username, String password) {
		try {
			Account account = findActiveAccountByUsername(username);

			if(account != null) {
				System.out.println();
				return crypt.checkpw(password, account.getPassword());
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean login(String username, String password) {
		return accountRepository.login(username, password, true) != null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = findActiveAccountByUsername(username);
		if(account == null) {
			throw new UsernameNotFoundException("Username not found");
		} else {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority(account.getTypeAccount().getName()));
			return new User(account.getUsername(), account.getPassword() ,authorities);
		}
	}

	@Override
	public AccountDTO findById(int id) {
		// TODO Auto-generated method stub
		return mapper.map(accountRepository.findById(id), new AccountDTO().getClass());
	}

	@Override
	public AccountDTO findByEmail(String email) {
		// TODO Auto-generated method stub
		return toDto(accountRepository.findByEmail(email));
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		try {
			accountRepository.delete(accountRepository.findById(id).get());
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public AccountDTO findByUsername(String username) {
		return toDto(findActiveAccountByUsername(username));
	}

	@Override
	public AccountDTO findByUsernameAndSecurityCode(String username, String securityCode) {
		return toDto(accountRepository.findFirstByUsernameIgnoreCaseAndSecurityCodeOrderByIdDesc(username, securityCode));
	}

	   @Override
	    public Iterable<Account> getAll() {
	        return accountRepository.findAll();
	    }

	    @Override
	    public Account getDetail(int id) {
	        return accountRepository.findById(id).get();
	    }

	    @Override
	    public Account find(int id) {
	        return accountRepository.findById(id).get();
	    }
	    @Transactional
	    @Override
	    public Boolean updateStatusById(int id, Boolean status) {
	        return accountRepository.updateStatusById(id, status) > 0;
	    }
	    

		@Override
		public Account getByUsername(String username) {
			return findActiveAccountByUsername(username);
		}

	@Override
	public int countByRoleAndMonthAndYear(int roleId, int month, int year) {
		if(accountRepository.countByRoleAndMonthAndYear(roleId, month, year) > 0){
			return accountRepository.countByRoleAndMonthAndYear(roleId, month, year);
		}else{
			return 0;
		}
	}

	@Override
	public int countByRole(int roleId) {
		if(accountRepository.countByRole(roleId) > 0){
			return accountRepository.countByRole(roleId);
		}else{
			return 0;
		}
	}

	private Account findActiveAccountByUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}
		return accountRepository.findFirstByUsernameIgnoreCaseAndStatusOrderByIdDesc(username.trim(), true);
	}

	private AccountDTO toDto(Account account) {
		if (account == null) {
			return null;
		}
		return mapper.map(account, AccountDTO.class);
	}
}
