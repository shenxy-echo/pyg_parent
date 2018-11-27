package com.pyg.service;

import com.pyg.pojo.TbSeller;
import com.pyg.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * created by 沈小云 on 2018/10/12  20:33
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username:"+username);
        List<GrantedAuthority> grantedAuthorities
                = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        TbSeller tbSeller = sellerService.findOne(username);
        System.out.println("password:"+tbSeller.getPassword());
        if(tbSeller != null){
           if("1".equals(tbSeller.getStatus())){
                return new User(username,tbSeller.getPassword(),grantedAuthorities);
           }else{
               return null;
           }
        }else{
            return null;
        }
    }
}
