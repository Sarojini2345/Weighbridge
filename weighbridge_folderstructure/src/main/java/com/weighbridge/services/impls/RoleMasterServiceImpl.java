package com.weighbridge.services.impls;

import com.weighbridge.dtos.RoleMasterDto;
import com.weighbridge.entities.RoleMaster;
import com.weighbridge.exceptions.ResourceCreationException;
import com.weighbridge.exceptions.ResourceNotFoundException;
import com.weighbridge.repsitories.RoleMasterRepository;
import com.weighbridge.services.RoleMasterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleMasterServiceImpl implements RoleMasterService {

    private final RoleMasterRepository roleMasterRepository;
    private final ModelMapper modelMapper;
    @Override
    public RoleMasterDto createRole(RoleMasterDto roleDto) {
        RoleMaster role = modelMapper.map(roleDto, RoleMaster.class);

        RoleMaster savedRole = null;
        try {
            savedRole = roleMasterRepository.save(role);
        } catch (Exception e) {
            throw new ResourceCreationException("Failed to create role",e);
        }

        return modelMapper.map(savedRole, RoleMasterDto.class);
    }

    @Override
    public void deleteRole(int roleId) {
        try {
            RoleMaster roleMaster = roleMasterRepository.findById(roleId).get();
            if(roleMaster.getRoleStatus()=="ACTIVE"){
                roleMaster.setRoleStatus("INACTIVE");
                roleMasterRepository.save(roleMaster);

            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Role","roleName", roleMasterRepository.findRoleNameByRoleId(roleId));
        }
    }

    @Override
    public List<String> getAllStringRole() {

        try{
            List<String> allRoleListName = roleMasterRepository.findAllRoleListName();
            return allRoleListName;
        }
        catch (Exception e){
            throw new ResourceNotFoundException("Failed to retrieve roles");
        }

    }

    @Override
    public List<RoleMasterDto> getAllRole() {
        try {
            List<RoleMaster> allRoles = roleMasterRepository.findAll();
            return allRoles.stream()
                    .map(roleMaster -> modelMapper.map(roleMaster, RoleMasterDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to retrieve roles");
        }

    }
}
