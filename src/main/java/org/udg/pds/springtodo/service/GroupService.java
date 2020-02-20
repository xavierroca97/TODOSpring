package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Group;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.CRUDRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class GroupService {

  @Autowired
  CRUDRepository groupRepository;

  @Autowired
  UserService userService;

  public CRUDRepository crud() {
    return groupRepository;
  }

  public Collection<Group> getGroups(Long id) {
    Optional<User> u = userService.crud().findById(id);
    if (!u.isPresent()) throw new ServiceException("User does not exists");
    return u.get().getGroups();
  }

  public Group getGroup(Long userId, Long id) {
      Optional<Group> t = groupRepository.findById(id);
      if (!t.isPresent()) throw new ServiceException("Group does not exists");
      if (t.get().getUser().getId() != userId)
        throw new ServiceException("User does not own this group");
      return t.get();
  }

  @Transactional
  public IdObject addGroup(String name, String description, Long userId) {
    try {
      User user = userService.getUser(userId);

      Group group = new Group(name, description);

      group.setUser(user);

      user.addGroup(group);

      groupRepository.save(group);
      return new IdObject(group.getId());
    } catch (Exception ex) {
      // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
      // We catch the normal exception and then transform it in a ServiceException
      throw new ServiceException(ex.getMessage());
    }
  }

}
