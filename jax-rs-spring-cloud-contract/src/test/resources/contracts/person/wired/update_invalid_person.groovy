package contracts.person.wired

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should not update a person with invalid data"

    request {
        url "/person"
        method PUT()
        headers {
            contentType applicationJson()
        }
        body (
                id: 25,
                firstName: "",
                lastName: "Hacker"
        )
    }

    response {
        status BAD_REQUEST()
        headers {
            contentType applicationJson()
        }
        body([
                code: 'BAD_REQUEST',
                message : $(producer(execute('contains($it, "update.arg0.firstName firstName is not allowed to be empty")')))
        ])
    }
}
