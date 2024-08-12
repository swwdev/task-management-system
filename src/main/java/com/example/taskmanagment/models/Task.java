package com.example.taskmanagment.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = {"header", "description"})
@EqualsAndHashCode(of = {"header", "description", "id"})
@Entity
public class Task {
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "task")
    private List<Comment> comments;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String header;
    private String description;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    @Enumerated(EnumType.ORDINAL)
    private Priority priority;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private User author;
    @ManyToMany
    @JoinTable(
            name = "task_assignee",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "assignee_id")
    )
    private Set<User> assignees;
}
