package com.example.services;

import com.example.data.TODODto;
import com.example.data.TODOItem;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;

// bugfix_1

// bugfix_2

/**
 * TODO Manager REST Controller
 * @author Pavel Shatrov
 */
@RestController
@RequestMapping(path = "/todo", produces = MediaType.APPLICATION_JSON_VALUE)
public class TODORestController {
    
    private final TODORepository todoRepository;

    public TODORestController(TODORepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    
    @CrossOrigin
    @RequestMapping(method = GET)
    public List<TODOItem> list() {
        return todoRepository.findAll();
    }
    
    @CrossOrigin
    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<TODOItem> get(@PathVariable Long id) {
        TODOItem item = this.todoRepository.findOne(id);
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.notFound().build();
    }
    
    @CrossOrigin
    @RequestMapping(value = "/update/{id}", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TODOItem> update(@PathVariable String id, @RequestBody TODODto input) {
        TODOItem item = todoRepository.findOne(Long.parseLong(id));
        if(item != null)
        {
            item.setDescription(input.getDescription());
            item.setIsDone(input.isIsDone());
            todoRepository.save(item);
        }
        return ResponseEntity.ok(item);
    }
    
    @CrossOrigin
    @RequestMapping(value = "/save", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TODOItem> save(@RequestBody TODODto input) {
        return ResponseEntity
                .ok(
                        this.todoRepository.save(
                                new TODOItem(
                                        input.getDescription(),
                                        input.isIsDone())
                        )
                );
    }
    @CrossOrigin
    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity delete(@PathVariable String id) {       
        todoRepository.delete(Long.parseLong(id));
        return ResponseEntity.ok().build();
    }
    
}

