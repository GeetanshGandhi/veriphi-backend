package com.project.veriphi.ticket;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResoldTicketRepository extends MongoRepository<ResoldTicket, String> {
}
